package com.udipoc.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RsOtpRequest {

    @JsonProperty("id")
    private String id;
    @JsonProperty("individualId")
    private String individualId;
    @JsonProperty("individualIdType")
    private String individualIdType;
    @JsonProperty("metadata")
    private Object metadata;
    @JsonProperty("otpChannel")
    private String[] otpChannel;
    @JsonProperty("requestTime")
    private String requestTime;
    @JsonProperty("transactionID")
    private String transactionID;
    @JsonProperty("version")
    private String version;
}
