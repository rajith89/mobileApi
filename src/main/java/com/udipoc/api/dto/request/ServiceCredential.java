package com.udipoc.api.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceCredential {
    @JsonProperty("appId")
    private String appId;
    @JsonProperty("clientId")
    private String clientId;
    @JsonProperty("secretKey")
    private String secretKey;
}
