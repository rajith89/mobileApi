package com.udipoc.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsAuthModeResponse implements Serializable {

    private static final long serialVersionUID = 7156526077883281623L;

    @JsonProperty("id")
    private String id;
    @JsonProperty("version")
    private String version;
    @JsonProperty("responsetime")
    private String responseTime;
    @JsonProperty("errors")
    private ArrayList<RsError> errors;
    @JsonProperty("response")
    private HashMap<String, String> response;
    @JsonProperty("metadata")
    private Object metadata;
}
