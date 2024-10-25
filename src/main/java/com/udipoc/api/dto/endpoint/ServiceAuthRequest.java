package com.udipoc.api.dto.endpoint;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceAuthRequest<T> {
    @JsonProperty("id")
    private String id;
    @JsonProperty("metadata")
    private Object metadata;
    @JsonProperty("request")
    private T request;
    @JsonProperty("requesttime")
    private String requesttime;
    @JsonProperty("version")
    private String version;


}
