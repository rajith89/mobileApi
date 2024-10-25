package com.udipoc.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/*This class contains common variables for the requests to be used in resident services*/
@Data
public class RsRequest<T> {

    @JsonProperty("id")
    private String id;
    @JsonProperty("version")
    private String version;
    @JsonProperty("requesttime")
    private String requestTime;
    @JsonProperty("request")
    private T request;
}
