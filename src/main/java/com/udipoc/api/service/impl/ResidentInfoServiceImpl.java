package com.udipoc.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udipoc.api.config.exception.ApiException;
import com.udipoc.api.config.exception.InvalidDataException;
import com.udipoc.api.dto.request.RsRequest;
import com.udipoc.api.dto.response.*;
import com.udipoc.api.repository.UserSessionRepository;
import com.udipoc.api.service.CommonService;
import com.udipoc.api.service.ResidentInfoService;
import com.udipoc.api.util.Constant;
import com.udipoc.api.util.enums.ErrorCode;
import com.udipoc.api.util.enums.ResponseCode;
import com.udipoc.api.util.enums.ServiceEndpoint;
import com.udipoc.api.util.enums.ServiceName;
import com.udipoc.api.util.image.CbeffToBiometricUtil;
import com.udipoc.api.util.image.JP2ImageConverter;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.core.cbeffutil.spi.CbeffUtil;
import io.mosip.kernel.core.util.CryptoUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class ResidentInfoServiceImpl implements ResidentInfoService {

    private final UserSessionRepository userSessionRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(ResidentInfoServiceImpl.class);
    private final RestTemplate restTemplate;
    private final CommonService commonService;
    @Autowired
    private CbeffUtil cbeffutil;
    @Autowired
    private CbeffToBiometricUtil cbeffToBiometricUtil;
    @Autowired
    private JP2ImageConverter jp2ImageConverter;

    @Autowired
    public ResidentInfoServiceImpl(UserSessionRepository userSessionRepository, RestTemplate restTemplate, CommonService commonService) {
        this.userSessionRepository = userSessionRepository;
        this.restTemplate = restTemplate;
        this.commonService = commonService;
    }

    @Autowired
    ServiceEndpoint serviceEndpoint;

    @Override
    public ResidentInfoResponse getDemographicData(String request) throws ApiException, InvalidDataException {
        if (request == null) {
            log.info("Empty request for resident info ");
            throw new InvalidDataException(ResponseCode.EMPTY_REQUEST_ERROR);
        }

        ResidentInfoResponse residentInfoResponse = new ResidentInfoResponse();
        residentInfoResponse.setStatus(ResponseCode.FAIL_RESIDENT_INFO.getDescription());
        List<ErrorDTO> errorDTOList;

        try {
            errorDTOList = new ArrayList();
            residentInfoResponse.setErrors(errorDTOList);

            Map<String, String> uriParam = new HashMap<>();
            uriParam.put("UIN", request);

            HttpHeaders httpHeaders = commonService.getClientServiceAuthentication(ServiceName.RESIDENT_SERVICE);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RsRequest> entity = new HttpEntity<RsRequest>(httpHeaders);

            ResponseEntity<IdResponse> responseEntity = restTemplate.exchange(serviceEndpoint.getRequestResidentInfoEndpoint(), HttpMethod.GET, entity, IdResponse.class, uriParam);
            if (responseEntity == null || responseEntity.getStatusCode() != HttpStatus.OK || !(responseEntity.getBody() instanceof IdResponse)) {
                log.error("response from mosip api getting error");
                throw new ApiException(ResponseCode.MOSIP_API_EXCEPTION);
            }
            if (responseEntity.getBody().getErrors() != null && responseEntity.getBody().getErrors().size() > 0) {
                ErrorDTO errorDTO = responseEntity.getBody().getErrors().get(0);
                if (errorDTO.getErrorCode().equals(Constant.MOSIP_ERROR_INVALID_UIN_IDREPO)) {
                    log.error("invalid uin");
                    throw new InvalidDataException(ResponseCode.INVALID_UIN);
                }
                log.error("response from mosip api contains errors");
                throw new ApiException(ResponseCode.MOSIP_API_ERROR_EXCEPTION);
            }

            IdResponse response = responseEntity.getBody();

            JSONObject demographicIdentity = convertIdentityMaptoJson((Map<String, Object>) response.getResponse().getIdentity());
            String jpegImageUrl = getFaceImageURL(response.getResponse().getDocuments(), residentInfoResponse);
            List documentsArray = loadDocuments(response.getResponse().getDocuments(), jpegImageUrl);

            residentInfoResponse.setIdentityObject(demographicIdentity);
            residentInfoResponse.setDocumentsArray(documentsArray);
            residentInfoResponse.setStatus(ResponseCode.SUCCESS_RESIDENT_INFO.getDescription());

        } catch (HttpClientErrorException httpClientErrorException) {
            log.error("Http Client Exception Error in getDemographicData-", httpClientErrorException.getLocalizedMessage());
            throw new ApiException(ResponseCode.MOSIP_API_401_EXCEPTION);
        } catch (InvalidDataException invalidDataException) {
            throw invalidDataException;
        } catch (ApiException apiException) {
            throw apiException;
        } catch (Exception exception) {
            log.error("General Exception in getDemographicData-", exception.getLocalizedMessage());
            log.debug(exception.getMessage());
            throw new ApiException(ResponseCode.GENERAL_EXCEPTION);
        }
        return residentInfoResponse;
    }

    private JSONObject convertIdentityMaptoJson(Map<String, Object> fieldMap) throws IOException, JSONException {
        JSONObject demographicIdentity = new JSONObject();
        for (Map.Entry e : fieldMap.entrySet()) {
            ObjectMapper mapper = new ObjectMapper();
            if (e.getValue() != null) {
                String value = mapper.writeValueAsString(e.getValue());
                if (value != null) {
                    Object json = new JSONTokener(value).nextValue();
                    if (json instanceof org.json.JSONObject) {
                        HashMap<String, Object> hashMap = mapper.readValue(value, HashMap.class);
                        demographicIdentity.putIfAbsent(e.getKey(), hashMap);
                    } else if (json instanceof JSONArray) {
                        List jsonList = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject();
                        JSONArray jsonArray = new JSONArray(value);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Object obj = jsonArray.get(i);
                            HashMap<String, Object> hashMap = mapper.readValue(obj.toString(), HashMap.class);
                            jsonList.add(hashMap);
                        }
                        if (jsonObject.size() > 0) {
                            demographicIdentity.putIfAbsent(e.getKey(), jsonObject);
                        } else {
                            demographicIdentity.putIfAbsent(e.getKey(), jsonList);
                        }
                    } else
                        demographicIdentity.putIfAbsent(e.getKey(), e.getValue());
                } else
                    demographicIdentity.putIfAbsent(e.getKey(), e.getValue());
            }
        }
        return demographicIdentity;
    }

    private List loadDocuments(List<DocumentsResponse> documentList, String jpegImageUrl) throws JSONException {
        List documentsArray = new ArrayList();
        for (DocumentsResponse documentsResponse : documentList) {
            JSONObject jsonObject = new JSONObject();
            if (documentsResponse.getCategory().equals("individualBiometrics")) {
                jsonObject.put("individualBiometrics", jpegImageUrl);
            } else {
                jsonObject.put(documentsResponse.getCategory(), documentsResponse.getValue());
            }
            documentsArray.add(jsonObject);
        }
        return documentsArray;
    }

    private String getIndividualBiometricsValue(List<DocumentsResponse> documentsResponseList) throws Exception {
        String individualBiometricsValue = "";
        for (DocumentsResponse documentsResponse : documentsResponseList) {
            if (documentsResponse.getCategory().equals("individualBiometrics")) {
                individualBiometricsValue = documentsResponse.getValue();
                break;
            }
        }
        return individualBiometricsValue;
    }

    private String getFaceImageURL(List<DocumentsResponse> documentsResponseList, ResidentInfoResponse residentInfoResponse) {
        String jpegImageUrl = "Error while image extracting";
        try {
            String individualBiometricsValue = getIndividualBiometricsValue(documentsResponseList);

            List<BIRType> bIRTypeList = cbeffutil.getBIRDataFromXML(CryptoUtil.decodeBase64(individualBiometricsValue));
            List<String> subtype = new ArrayList<>();
            byte[] photoBytes = cbeffToBiometricUtil.getPhotoByTypeAndSubType(bIRTypeList, "FACE", subtype);

            jpegImageUrl = jp2ImageConverter.getjpegImageUrlByPhotoBytes(photoBytes);
        } catch (Exception e) {
            log.error("Error while extracting image-", e.getLocalizedMessage());
            log.debug(e.getMessage());
            jpegImageUrl = "Error while image extracting";
            ErrorDTO errorDTO = new ErrorDTO(ErrorCode.IMAGE_EXTRACTION_EXCEPTION.getCode(), ErrorCode.IMAGE_EXTRACTION_EXCEPTION.getDescription());
            residentInfoResponse.getErrors().add(errorDTO);
        }
        return jpegImageUrl;
    }
}
