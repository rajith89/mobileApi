package com.udipoc.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RsAuthModeRequest {

    @JsonProperty("transactionID")
    private String transactionId;
    @JsonProperty("individualId")
    private String individualId;
    @JsonProperty("individualIdType")
    private String individualIdType;
    @JsonProperty("otp")
    private String otp;
    @JsonProperty("authType")
    private List<String> authType;
}
