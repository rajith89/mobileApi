package com.udipoc.api.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResidentAuthHistoryRequest {
    private String id;
    private String version;
    private String requesttime;
    private AuthHistoryRequestBody request;
}
