package com.udipoc.api.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.udipoc.api.config.exception.ApiException;
import com.udipoc.api.config.exception.InvalidDataException;
import com.udipoc.api.dto.request.*;
import com.udipoc.api.dto.response.*;
import com.udipoc.api.service.CommonService;
import com.udipoc.api.service.ResidentService;
import com.udipoc.api.util.Constant;
import com.udipoc.api.util.DateUtils;
import com.udipoc.api.util.enums.ResponseCode;
import com.udipoc.api.util.enums.ServiceEndpoint;
import com.udipoc.api.util.enums.ServiceName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class ResidentServiceImpl implements ResidentService {

    private final CommonService commonService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ResidentServiceImpl(CommonService commonService, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.commonService = commonService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Autowired
    ServiceEndpoint serviceEndpoint;

    @Transactional
    @Override
    public MobileAuthModeResponse lockAuthModes(MobAuthModeRequest request) throws ApiException, InvalidDataException {

        if (request != null) {

            RsAuthModeRequest rsAuthModeRequest = new RsAuthModeRequest();
            rsAuthModeRequest.setIndividualIdType("UIN");
            rsAuthModeRequest.setIndividualId(request.getUin());
            rsAuthModeRequest.setOtp(request.getOtp());
            rsAuthModeRequest.setTransactionId(request.getTransactionId());
            rsAuthModeRequest.setAuthType(request.getAuthType());

            RsRequest rsRequest = new RsRequest();
            rsRequest.setId("mosip.resident.authlock");
            rsRequest.setRequestTime(DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
            rsRequest.setVersion("v1");
            rsRequest.setRequest(rsAuthModeRequest);


            RsAuthModeResponse rsAuthModeResponse = new RsAuthModeResponse();
            MobileAuthModeResponse mobileAuthModeResponse = new MobileAuthModeResponse();
            try {
                HttpHeaders httpHeaders = commonService.getClientServiceAuthentication(ServiceName.RESIDENT_SERVICE);
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<RsRequest> entity = new HttpEntity<RsRequest>(rsRequest, httpHeaders);
                ResponseEntity<String> responseEntity = restTemplate.exchange(serviceEndpoint.getRequestResidentAuthModeLockEndpoint(), HttpMethod.POST, entity, String.class);
                log.info("Mosip api response : " + responseEntity);
                if (responseEntity == null || responseEntity.getStatusCode() != HttpStatus.OK) {
                    log.error("response from mosip api getting error");
                    throw new ApiException(ResponseCode.MOSIP_API_EXCEPTION);
                }
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    rsAuthModeResponse = objectMapper.readValue(responseEntity.getBody(), new TypeReference<RsAuthModeResponse>() {
                    });
                    if (rsAuthModeResponse.getErrors() != null) {
                        RsError error = rsAuthModeResponse.getErrors().get(0);
                        if (error.getErrorCode().equals(Constant.MOSIP_ERROR_OTP_OR_TRANSACTION_ID_INVALID)) {
                            throw new InvalidDataException(ResponseCode.INVALID_OTP_OR_TRANSACTION_ID);
                        }
                        if (error.getErrorCode().equals(Constant.MOSIP_ERROR_INVALID_UIN_AUTHMODE)) {
                            throw new InvalidDataException(ResponseCode.INVALID_UIN);
                        }
                    }
                    mobileAuthModeResponse.setMessage(rsAuthModeResponse.getResponse().get("message"));
                }
            } catch (HttpClientErrorException httpClientErrorException) {
                log.error("Http Client Exception Error in Resident lockAuthModes : ", httpClientErrorException.getLocalizedMessage());
                throw new ApiException(ResponseCode.MOSIP_API_401_EXCEPTION);
            } catch (InvalidDataException invalidDataException) {
                throw invalidDataException;
            } catch (ApiException apiException) {
                throw apiException;
            } catch (Exception exception) {
                log.error("General Exception in lockAuthModes :", exception.getLocalizedMessage());
                throw new ApiException(ResponseCode.RESIDENT_AUTH_LOCK_FAILED);
            }
            return mobileAuthModeResponse;
        } else {
            log.info("Empty request for resident auth lock ");
            throw new InvalidDataException(ResponseCode.INVALID_REQUEST);
        }
    }

    @Override
    public MobileAuthModeResponse unlockAuthModes(MobAuthModeRequest request) throws ApiException, InvalidDataException {
        if (request != null) {

            RsAuthModeRequest rsAuthModeRequest = new RsAuthModeRequest();
            rsAuthModeRequest.setIndividualIdType("UIN");
            rsAuthModeRequest.setIndividualId(request.getUin());
            rsAuthModeRequest.setOtp(request.getOtp());
            rsAuthModeRequest.setTransactionId(request.getTransactionId());
            rsAuthModeRequest.setAuthType(request.getAuthType());

            RsRequest rsRequest = new RsRequest();
            rsRequest.setId("mosip.resident.authunlock");
            rsRequest.setRequestTime(DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
            rsRequest.setVersion("v1");
            rsRequest.setRequest(rsAuthModeRequest);


            RsAuthModeResponse rsAuthModeResponse = new RsAuthModeResponse();
            MobileAuthModeResponse mobileAuthModeResponse = new MobileAuthModeResponse();
            try {
                HttpHeaders httpHeaders = commonService.getClientServiceAuthentication(ServiceName.RESIDENT_SERVICE);
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<RsRequest> entity = new HttpEntity<RsRequest>(rsRequest, httpHeaders);
                ResponseEntity<String> responseEntity = restTemplate.exchange(serviceEndpoint.getRequestResidentAuthModeUnlockEndpoint(), HttpMethod.POST, entity, String.class);
                log.info("Mosip api response : " + responseEntity);
                if (responseEntity == null || responseEntity.getStatusCode() != HttpStatus.OK) {
                    log.error("response from mosip api getting error");
                    throw new ApiException(ResponseCode.MOSIP_API_EXCEPTION);
                }
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    rsAuthModeResponse = objectMapper.readValue(responseEntity.getBody(), new TypeReference<RsAuthModeResponse>() {
                    });
                    if (rsAuthModeResponse.getErrors() != null) {
                        RsError error = rsAuthModeResponse.getErrors().get(0);
                        if (error.getErrorCode().equals(Constant.MOSIP_ERROR_OTP_OR_TRANSACTION_ID_INVALID)) {
                            throw new InvalidDataException(ResponseCode.INVALID_OTP_OR_TRANSACTION_ID);
                        }
                        if (error.getErrorCode().equals(Constant.MOSIP_ERROR_INVALID_UIN_AUTHMODE)) {
                            throw new InvalidDataException(ResponseCode.INVALID_UIN);
                        }
                    }
                    mobileAuthModeResponse.setMessage(rsAuthModeResponse.getResponse().get("message"));
                }
            } catch (HttpClientErrorException httpClientErrorException) {
                log.error("Http Client Exception Error in Resident unlockAuthModes : ", httpClientErrorException.getLocalizedMessage());
                throw new ApiException(ResponseCode.MOSIP_API_401_EXCEPTION);
            } catch (InvalidDataException invalidDataException) {
                throw invalidDataException;
            } catch (ApiException apiException) {
                throw apiException;
            } catch (Exception exception) {
                log.error("General Exception in unlockAuthModes : ", exception.getLocalizedMessage());
                throw new ApiException(ResponseCode.RESIDENT_AUTH_UNLOCK_FAILED);
            }
            return mobileAuthModeResponse;
        } else {
            log.info("Empty request for resident auth lock ");
            throw new InvalidDataException(ResponseCode.INVALID_REQUEST);
        }
    }

    @Override
    public List<AuthHistoryResponse> getAuthHistory(AuthHistoryRequest authHistoryRequest) throws ApiException, InvalidDataException {
        List<AuthHistoryResponse> details = null;
        try{

            AuthHistoryResponse authHistoryResponse=null;
            int count = 1;
            log.info("Start getAuthHistory request");
            AutnTxnResponseDto autnTxnResponseDto = getAuthTxnHistory(authHistoryRequest.getIndividualId());
            if (autnTxnResponseDto.getErrors() == null) {
                log.info("Auth History Request Completed");
                if (autnTxnResponseDto.getResponse().get("authTransactions") != null) {
                    details = new ArrayList<AuthHistoryResponse>();
                    if (!autnTxnResponseDto.getResponse().get("authTransactions").isEmpty()) {
                        for (AutnTxnDto autnTxnDto : autnTxnResponseDto.getResponse().get("authTransactions")) {
                            authHistoryResponse=getDetails(autnTxnDto, count);
                            if(!authHistoryResponse.getAuthResponse().matches("(.*)errorCode(.*)")){
                                details.add(authHistoryResponse);
                                count++;
                            }
                        }
                    }
                }
            }else{
                if (autnTxnResponseDto.getErrors() != null) {
                    RsError error = autnTxnResponseDto.getErrors().get(0);
                    if (error.getErrorCode().equals(Constant.MOSIP_ERROR_INVALID_INDIVIDUALTYPE)) {
                        throw new InvalidDataException(ResponseCode.INVALID_UIN);
                    }
                }
            }
        }catch (HttpClientErrorException httpClientErrorException) {
            log.error("Http Client Exception Error in Resident unlockAuthModes : ", httpClientErrorException.getLocalizedMessage());
            throw new ApiException(ResponseCode.MOSIP_API_401_EXCEPTION);
        } catch (InvalidDataException invalidDataException) {
            throw invalidDataException;
        } catch (ApiException apiException) {
            throw apiException;
        } catch (Exception exception) {
            log.error("General Exception in unlockAuthModes : ", exception.getLocalizedMessage());
            throw new ApiException(ResponseCode.RESIDENT_AUTH_HISTORY_ERROR);
        }
        return details;
    }
    /* private ResponseEntity<AuthHistoryResponse> getAuthHistory(String uin, String transactionId, String otp) throws ApiException, InvalidDataException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = commonService.getClientServiceAuthentication(ServiceName.RESIDENT_SERVICE);

        ResidentAuthHistoryRequest residentAuthHistoryRequest = new ResidentAuthHistoryRequest();
        residentAuthHistoryRequest.setId("mosip.resident.authhistory");
        residentAuthHistoryRequest.setVersion("v1");
        residentAuthHistoryRequest.setRequesttime(LocalDateTime.now().toString());

        AuthHistoryRequestBody request = new AuthHistoryRequestBody();
        request.setTransactionID(transactionId);
        request.setIndividualId(uin);
        request.setIndividualIdType("UIN");
        request.setOtp(otp);

        residentAuthHistoryRequest.setRequest(request);

        HttpEntity<ResidentAuthHistoryRequest> entity = new HttpEntity<>(residentAuthHistoryRequest, httpHeaders);
        ResponseEntity<AuthHistoryResponse> result = restTemplate.exchange(serviceEndpoint.getRequestAuthHistoryEndpoint(), HttpMethod.POST, entity, AuthHistoryResponse.class);
        return result;
    }*/

    private AutnTxnResponseDto getAuthTxnHistory(String uin) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = commonService.getClientServiceAuthentication(ServiceName.RESIDENT_SERVICE);
        Map<String, String> uriParam = new HashMap<String, String>();
        uriParam.put("uin", uin);

        /*
        * As a future enhancement we can add pagination also
        * */
        HttpEntity<ResidentAuthHistoryRequest> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<AutnTxnResponseDto> result = restTemplate.exchange(serviceEndpoint.getRequestAuthHistoryEndpoint(), HttpMethod.GET, entity, AutnTxnResponseDto.class, uriParam);
        log.info("Mosip Response : "+result.getBody().toString());
        return result.getBody();
    }

    private AuthHistoryResponse getDetails(AutnTxnDto autnTxnDto, int count) {
        AuthHistoryResponse authTxnDetailsDTO = new AuthHistoryResponse();
        authTxnDetailsDTO.setSerialNumber(count);
        authTxnDetailsDTO.setAuthModality(autnTxnDto.getAuthtypeCode());
        authTxnDetailsDTO.setAuthResponse(autnTxnDto.getStatusComment());
        authTxnDetailsDTO.setIdUsed(autnTxnDto.getReferenceIdType());
        authTxnDetailsDTO.setPartnerName(autnTxnDto.getEntityName());
        authTxnDetailsDTO.setPartnerTransactionId(autnTxnDto.getTransactionID());
        authTxnDetailsDTO.setResponseCode(autnTxnDto.getStatusCode());

        /* *
        * Convert utc time into local time
        */
        ZoneId colomboTimeZone = ZoneId.of("Asia/Colombo");
        ZoneId utcTimeZone = ZoneId.of("UTC");
        ZonedDateTime colomboDateTime = autnTxnDto.getRequestdatetime().atZone(utcTimeZone).withZoneSameInstant(colomboTimeZone);

        authTxnDetailsDTO.setDate(colomboDateTime.toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
        authTxnDetailsDTO.setTime(colomboDateTime.toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_TIME));

        return authTxnDetailsDTO;
    }
}
