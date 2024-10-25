package com.udipoc.api.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.udipoc.api.config.exception.ApiException;
import com.udipoc.api.config.exception.DeviceNotValidException;
import com.udipoc.api.config.exception.InvalidDataException;
import com.udipoc.api.config.security.JwtAuthenticationProvider;
import com.udipoc.api.dto.endpoint.UserDeviceDto;
import com.udipoc.api.dto.request.*;
import com.udipoc.api.dto.response.*;
import com.udipoc.api.entity.UserDevice;
import com.udipoc.api.entity.UserSession;
import com.udipoc.api.repository.UserDeviceRepository;
import com.udipoc.api.repository.UserSessionRepository;
import com.udipoc.api.service.AuthService;
import com.udipoc.api.service.CommonService;
import com.udipoc.api.util.Constant;
import com.udipoc.api.util.DateUtils;
import com.udipoc.api.util.MathUtils;
import com.udipoc.api.util.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final CommonService commonService;
    private final RestTemplate restTemplate;
    private final UserSessionRepository userSessionRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtAuthenticationProvider tokenProvider;
    private final ObjectMapper objectMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserDeviceRepository userDeviceRepository;

    @Value("${auth.otp.attempts}")
    private int otpAttempts;

    @Value("${auth.device.block.reset.minutes}")
    private int deviceUnblockInMinutes;

    @Autowired
    ServiceEndpoint serviceEndpoint;

    @Autowired
    public AuthServiceImpl(CommonService commonService, RestTemplate restTemplate,
                           UserSessionRepository userSessionRepository, AuthenticationManager authenticationManager,
                           ObjectMapper objectMapper, BCryptPasswordEncoder passwordEncoder,
                           JwtAuthenticationProvider tokenProvider, UserDeviceRepository userDeviceRepository) {
        this.commonService = commonService;
        this.restTemplate = restTemplate;
        this.userSessionRepository = userSessionRepository;
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.userDeviceRepository = userDeviceRepository;
    }

    @Override
    @Transactional
    public MobOtpResponse requestOtp(MobOtpRequest mobOtpRequest) throws ApiException, InvalidDataException, DeviceNotValidException {

        if (mobOtpRequest != null) {
            /* Check the uin is used in another device*/
            if(isUinLoggedInWithAnotherDevice(mobOtpRequest.getDeviceId(),mobOtpRequest.getUin())){
                throw  new DeviceNotValidException(ResponseCode.USER_UIN_LOGGED_IN_ANOTHER_DEVICE);
            }
            UserDeviceDto isBlockedUserDevice = isUserDeviceBlocked(mobOtpRequest.getDeviceId());

            /* If the user device is blocked in less than the defined time, disable loging into the application */
            if (isBlockedUserDevice.isBlocked()) {
                Duration duration = Duration.between(isBlockedUserDevice.getAttemptTime(), LocalDateTime.now());
                if(duration.toMinutes()<deviceUnblockInMinutes){
                    throw new DeviceNotValidException(ResponseCode.USER_DEVICE_BLOCKED);
                }
                resetDeviceAttempts(mobOtpRequest.getDeviceId(),mobOtpRequest.getUin());
            }
            /** Retrieve UIN from id repo and check that given UIN is valid or not before sending otp req */
            IdResponse validUin=isValidUin(mobOtpRequest.getUin());

            /** Validate user login attempts */
            validateAttempts(validUin, mobOtpRequest.getDeviceId(), mobOtpRequest.getUin(), isBlockedUserDevice);

            if(validUin.getErrors()!=null && validUin.getErrors().size()>0){
                ErrorDTO errorDTO = validUin.getErrors().get(0);
                if (errorDTO.getErrorCode().equals(Constant.MOSIP_ERROR_INVALID_UIN_IDREPO)) {
                    log.error("invalid uin");
                    throw new InvalidDataException(ResponseCode.INVALID_UIN);
                }
                log.error("response from mosip api contains errors");
                throw new ApiException(ResponseCode.MOSIP_API_ERROR_EXCEPTION);
            }
            if(isDeviceLoggedInWithAnotherUin(mobOtpRequest.getDeviceId(), mobOtpRequest.getUin())){
                throw new DeviceNotValidException(ResponseCode.USER_DEVICE_LOGGED_IN_ANOTHER_UIN);
            }

            RsOtpRequest rsOtpRequest = new RsOtpRequest();
            rsOtpRequest.setId("mosip.identity.otp.internal");
            rsOtpRequest.setIndividualId(mobOtpRequest.getUin());
            rsOtpRequest.setIndividualIdType("UIN");
            rsOtpRequest.setOtpChannel(new String[]{"PHONE", "EMAIL"});
            rsOtpRequest.setRequestTime(DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
            rsOtpRequest.setTransactionID(MathUtils.generateRandomNumber());
            rsOtpRequest.setVersion("1.0");

            RsOtpResponse rsOtpResponse = new RsOtpResponse();
            MobOtpResponse mobOtpResponse = new MobOtpResponse();
            try {
                HttpHeaders httpHeaders = commonService.getClientServiceAuthentication(ServiceName.RESIDENT_SERVICE);
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<RsOtpRequest> entity = new HttpEntity<RsOtpRequest>(rsOtpRequest, httpHeaders);

                ResponseEntity<String> responseEntity = restTemplate.exchange(serviceEndpoint.getRequestOtpEndpoint(), HttpMethod.POST, entity, String.class);
                log.info("Mosip api response : " + responseEntity);
                if (responseEntity == null || responseEntity.getStatusCode() != HttpStatus.OK) {
                    log.error("response from mosip api getting error");
                    throw new ApiException(ResponseCode.MOSIP_API_EXCEPTION);
                }
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    rsOtpResponse = objectMapper.readValue(responseEntity.getBody(), new TypeReference<RsOtpResponse>() {
                    });
                    if (rsOtpResponse.getErrors() != null) {
                        RsError error = rsOtpResponse.getErrors().get(0);
                        if (error.getErrorCode().equals(Constant.MOSIP_ERROR_INVALID_UIN)) {
                            throw new InvalidDataException(ResponseCode.INVALID_UIN);
                        }
                        if (error.getErrorCode().equals(Constant.MOSIP_ERROR_OTP_FAILED)) {
                            throw new InvalidDataException(ResponseCode.FAIL_OTP_REQUEST);
                        }
                    }

                    if (!rsOtpResponse.getResponse().isEmpty() && !rsOtpResponse.getResponse().get("maskedMobile").isEmpty()) {
                        mobOtpResponse.setMobile(rsOtpResponse.getResponse().get("maskedMobile"));
                        mobOtpResponse.setTransactionId(rsOtpRequest.getTransactionID());
                    } else {
                        throw new InvalidDataException(ResponseCode.MOBILENO_OR_TRANSACTIONID_NOT_FOUND);
                    }
                }
            } catch (HttpClientErrorException httpClientErrorException) {
                log.error("Http Client Exception Error in requestOtp : ", httpClientErrorException.getLocalizedMessage());
                throw new ApiException(ResponseCode.MOSIP_API_401_EXCEPTION);
            } catch (InvalidDataException invalidDataException) {
                throw invalidDataException;
            } catch (ApiException apiException) {
                throw apiException;
            } catch (Exception exception) {
                log.error("General Exception in requestOtp : ", exception.getLocalizedMessage());
                throw new ApiException(ResponseCode.GENERAL_EXCEPTION);
            }

            return mobOtpResponse;
        }
        throw new InvalidDataException(ResponseCode.INVALID_REQUEST);
    }

    @Override
    @Transactional
    public MobLoginRequest validateOtpRequest(MobOtpValidateRequest mobOtpValidateRequest) throws ApiException, InvalidDataException {

        MobLoginRequest mobLoginRequest = new MobLoginRequest();
        RsOtpValidateResponse rsOtpValidateResponse = new RsOtpValidateResponse();
        String status = "";
        try {
            HttpHeaders httpHeaders = commonService.getClientServiceAuthentication(ServiceName.RESIDENT_SERVICE);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serviceEndpoint.getRequestOtpValidateEndpoint())
                    .queryParam("key", mobOtpValidateRequest.getUin())
                    .queryParam("otp", mobOtpValidateRequest.getOtp());

            HttpEntity<RsOtpRequest> entity = new HttpEntity<RsOtpRequest>(httpHeaders);
            ResponseEntity<String> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
            log.info("Mosip api response : " + responseEntity);
            if (responseEntity == null || responseEntity.getStatusCode() != HttpStatus.OK) {
                log.error("response from mosip api getting error");
                throw new ApiException(ResponseCode.MOSIP_API_EXCEPTION);
            }
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                rsOtpValidateResponse = objectMapper.readValue(responseEntity.getBody(), new TypeReference<RsOtpValidateResponse>() {
                });
                if (rsOtpValidateResponse.getErrors() != null) {
                    RsError error = rsOtpValidateResponse.getErrors().get(0);
                    if (error.getErrorCode().equals(Constant.MOSIP_ERROR_INVALID_UIN)) {
                        throw new InvalidDataException(ResponseCode.INVALID_UIN);
                    }
                    if (error.getErrorCode().equals(Constant.MOSIP_ERROR_OTP_FAILED)) {
                        throw new InvalidDataException(ResponseCode.FAIL_OTP_REQUEST);
                    }
                    if (rsOtpValidateResponse.getResponse().get("message").equals(Constant.MOSIP_ERROR_OTP_EXPIRED)) {
                        throw new InvalidDataException(ResponseCode.OTP_EXPIRED);
                    }
                    if (rsOtpValidateResponse.getResponse().get("message").equals(Constant.MOSIP_ERROR_OTP_VALIDATION_FAILED)) {
                        throw new InvalidDataException(ResponseCode.INVALID_OTP);
                    }
                    if (error.getErrorCode().equals(Constant.GENERATE_OTP_FIRST)) {
                        throw new InvalidDataException(ResponseCode.FAIL_OTP_INVALIDATE);
                    }
                }
                status = rsOtpValidateResponse.getResponse().get("status");
                if (status.equals(Constant.SUCCESS)) {

                    //bind device id with uin
                    updateDeviceLogin(mobOtpValidateRequest.getDeviceId());

                    UserSession userSession = userSessionRepository.findUserSessionByUsernameAndStatus(mobOtpValidateRequest.getUin(), Status.ACTIVE);
                    if (userSession == null) {
                        UserSession session = new UserSession();
                        session.setUsername(mobOtpValidateRequest.getUin());
                        session.setPassword(passwordEncoder.encode(mobOtpValidateRequest.getUin()));
                        session.setStatus(Status.ACTIVE);
                        userSessionRepository.save(session);
                        mobLoginRequest.setUsername(session.getUsername());
                        mobLoginRequest.setPassword(session.getUsername());
                    } else {
                        mobLoginRequest.setUsername(userSession.getUsername());
                        mobLoginRequest.setPassword(userSession.getUsername());
                    }
                } else {
                    throw new InvalidDataException(ResponseCode.OTP_EXPIRED);
                }
            }
        } catch (HttpClientErrorException httpClientErrorException) {
            log.error("Http Client Exception Error in validateOtpRequest : ", httpClientErrorException.getLocalizedMessage());
            throw new ApiException(ResponseCode.MOSIP_API_401_EXCEPTION);
        } catch (InvalidDataException invalidDataException) {
            throw invalidDataException;
        } catch (ApiException apiException) {
            throw apiException;
        } catch (Exception exception) {
            log.error("General Exception in validateOtpRequest : ", exception.getLocalizedMessage());
            throw new ApiException(ResponseCode.GENERAL_EXCEPTION);
        }
        return mobLoginRequest;
    }

    @Override
    @Transactional
    public MobAuthResponse userAuthenticate(MobLoginRequest mobLoginRequest) throws ApiException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(mobLoginRequest.getUsername(), mobLoginRequest.getPassword()));
        } catch (DisabledException e) {
            throw new ApiException(ResponseCode.FAIL_USER_DISABLED);
        } catch (BadCredentialsException e) {
            throw new ApiException(ResponseCode.FAIL_AUTH);
        }
        Optional<UserSession> userSession = userSessionRepository.findByUsername(mobLoginRequest.getUsername(), Status.ACTIVE);
        if (userSession.isPresent()) {
            log.info("Authentication succeed");
            MobAuthResponse mobAuthResponse = new MobAuthResponse();
            mobAuthResponse.setToken(tokenProvider.generateToken(userSession.get().getUsername()));
            mobAuthResponse.setType("Bearer");
            mobAuthResponse.setUsername(userSession.get().getUsername());
            userSession.get().setToken(mobAuthResponse.getToken());
            userSession.get().setLoginStatus(Status.ACTIVE);
            userSessionRepository.save(userSession.get());
            return mobAuthResponse;
        } else {
            throw new ApiException(ResponseCode.FAIL_AUTH);
        }
    }

    @Override
    public MobAuthUnblockResponse userUnblock(String deviceId, String uin) throws ApiException, InvalidDataException {

        if (StringUtils.hasText(deviceId) && StringUtils.hasText(uin)) {
            resetDeviceAttempts(deviceId, uin);
            MobAuthUnblockResponse mobAuthUnblockResponse = new MobAuthUnblockResponse();
            mobAuthUnblockResponse.setMessage("Successfully unblocked user");
            return mobAuthUnblockResponse;
        } else {
            log.info("Device Id can not be empty or null ");
            throw new InvalidDataException(ResponseCode.INVALID_REQUEST);
        }
    }

    private UserDeviceDto isUserDeviceBlocked(String deviceId) {
        Optional<UserDevice> existingDeviceRecord = isDeviceExists(deviceId);
        UserDeviceDto userDeviceDto = new UserDeviceDto();

        if (!existingDeviceRecord.isPresent()) {
            userDeviceDto.setBlocked(false);
            userDeviceDto.setDeviceExists(false);
            userDeviceDto.setTimeDurationValid(true);
            return userDeviceDto;
        }

        UserDevice userDevice = existingDeviceRecord.get();
        userDeviceDto.setDeviceExists(true);
        userDeviceDto.setUserDevice(userDevice);

        Duration duration = Duration.between(userDevice.getAttemptTime(), LocalDateTime.now());
        boolean isTimeDurationValid = duration.toHours() > 1;
        userDeviceDto.setTimeDurationValid(isTimeDurationValid);

        if (userDevice.getAttempts() < otpAttempts || isTimeDurationValid) {
            userDeviceDto.setBlocked(false);
            return userDeviceDto;
        }
        userDevice.setIsBlocked(BlockType.TRUE);
        userDeviceDto.setBlocked(true);
        userDeviceDto.setLoggedIn(userDevice.isLoggedIn());
        userDeviceDto.setAttemptTime(userDevice.getAttemptTime());
        return userDeviceDto;
    }

    private void validateAttempts(IdResponse idResponse, String deviceId, String uin, UserDeviceDto userDeviceBlocked) {
        boolean isFailedCurrentAttempt = false;

        if (idResponse.getErrors() != null && !idResponse.getErrors().isEmpty() || isDeviceLoggedInWithAnotherUin(deviceId, uin)){
            isFailedCurrentAttempt = true;
        }

        UserDevice userDevice;
        if (userDeviceBlocked.isDeviceExists()) {
            userDevice = userDeviceBlocked.getUserDevice();
            int attempts = isFailedCurrentAttempt && userDeviceBlocked.isTimeDurationValid() ? 1 :
                    isFailedCurrentAttempt && !userDeviceBlocked.isTimeDurationValid() ? (userDeviceBlocked.getUserDevice().getAttempts() + 1) : 0;
            userDevice.setAttempts(attempts);
            userDevice.setIsBlocked(attempts < otpAttempts ? BlockType.FALSE : BlockType.TRUE);
            if(!userDevice.isLoggedIn()){
                userDevice.setUin(uin);
            }
        } else {
            userDevice = new UserDevice();
            userDevice.setId(null);
            userDevice.setDeviceId(deviceId);
            userDevice.setUin(uin);
            userDevice.setIsBlocked(BlockType.FALSE);
            userDevice.setAttempts(isFailedCurrentAttempt ? 1 : 0);
        }
//        if(!userDevice.isLoggedIn()){
//            userDevice.setLoggedIn(!isFailedCurrentAttempt);
//        }
        userDevice.setAttemptTime(LocalDateTime.now());
        updateUserDevice(userDevice);

    }

    private UserDevice updateUserDevice(UserDevice userDevice) {
        return userDeviceRepository.saveAndFlush(userDevice);
    }

    private Optional<UserDevice> isDeviceExists(String deviceId) {
        return userDeviceRepository.findByDeviceId(deviceId);
    }

    private Optional<UserDevice> isUinExists(String uin){
       return userDeviceRepository.findByUinAndIsLoggedIn(uin,true);
    }
    private UserDevice resetDeviceAttempts(String deviceId, String uin) throws InvalidDataException {
        Optional<UserDevice> existingDeviceRecord = isDeviceExists(deviceId);
        if (existingDeviceRecord.isPresent()) {
            UserDevice userDevice = existingDeviceRecord.get();
            if (userDevice.getAttempts() != otpAttempts) {
                throw new InvalidDataException(ResponseCode.USER_DEVICE_NOT_BLOCKED);
            }
           /* if (userDevice.getUin().equals(uin)) {
                throw new InvalidDataException(ResponseCode.USER_DEVICE_UNBLOCK_FAILED);
            }*/
            userDevice.setAttempts(0);
            userDevice.setAttemptTime(LocalDateTime.now());
            userDevice.setIsBlocked(BlockType.FALSE);
            return updateUserDevice(userDevice);
        }
        return null;
    }

    private boolean isDeviceLoggedInWithAnotherUin(String deviceId,String uin){
        Optional<UserDevice> existingDeviceRecord = isDeviceExists(deviceId);

        if (existingDeviceRecord.isPresent()) {
            UserDevice userDevice = existingDeviceRecord.get();
            if(!userDevice.getUin().equals(uin) && userDevice.isLoggedIn()) return true;
        }
        return false;
    }

    private boolean isUinLoggedInWithAnotherDevice(String deviceId, String uin){
        Optional<UserDevice> existingUinRecord = isUinExists(uin);
        if (existingUinRecord.isPresent()) {
            UserDevice userDevice = existingUinRecord.get();
            if(!userDevice.getDeviceId().equals(deviceId) && userDevice.isLoggedIn()) return true;
        }
        return false;
    }
    private void updateDeviceLogin(IdResponse idResponse, String deviceId){
        Optional<UserDevice> existingDeviceRecord = isDeviceExists(deviceId);
        if (existingDeviceRecord.isPresent()) {
            UserDevice userDevice = existingDeviceRecord.get();
            if(!userDevice.isLoggedIn() && (idResponse.getErrors()==null || idResponse.getErrors().isEmpty())){
                userDevice.setLoggedIn(true);
                updateUserDevice(userDevice);
            }
        }
    }

    private void updateDeviceLogin(String deviceId){
        Optional<UserDevice> existingDeviceRecord = isDeviceExists(deviceId);
        if (existingDeviceRecord.isPresent()) {
            UserDevice userDevice = existingDeviceRecord.get();
            if(!userDevice.isLoggedIn()){
                userDevice.setLoggedIn(true);
                updateUserDevice(userDevice);
            }
        }
    }

    private IdResponse isValidUin(String uin){
        IdResponse response=null;
        try {
            Map<String, String> uriParam = new HashMap<>();
            uriParam.put("UIN", uin);

            HttpHeaders httpHeaders = commonService.getClientServiceAuthentication(ServiceName.RESIDENT_SERVICE);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RsRequest> entity = new HttpEntity<RsRequest>(httpHeaders);

            ResponseEntity<IdResponse> responseEntity = restTemplate.exchange(serviceEndpoint.getRequestResidentInfoEndpoint(), HttpMethod.GET, entity, IdResponse.class, uriParam);
            if (responseEntity == null || responseEntity.getStatusCode() != HttpStatus.OK || !(responseEntity.getBody() instanceof IdResponse)) {
                log.error("response from mosip api getting error");
                throw new ApiException(ResponseCode.MOSIP_API_EXCEPTION);
            }
            response = responseEntity.getBody();

        } catch (HttpClientErrorException httpClientErrorException) {
            log.error("Http Client Exception Error in getDemographicData-", httpClientErrorException.getLocalizedMessage());
            throw new ApiException(ResponseCode.MOSIP_API_401_EXCEPTION);
       } catch (ApiException apiException) {
            throw apiException;
        } catch (Exception exception) {
            log.error("General Exception in getDemographicData-", exception.getLocalizedMessage());
            throw new ApiException(ResponseCode.GENERAL_EXCEPTION);
        }
        return response;
    }
}
