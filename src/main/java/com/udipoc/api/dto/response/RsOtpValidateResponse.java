package com.udipoc.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

@Data
public class RsOtpValidateResponse implements Serializable {

    private static final long serialVersionUID = 7156526077883281623L;

    @JsonProperty("id")
    private String id;
    @JsonProperty("version")
    private String version;
    @JsonProperty("responsetime")
    private String responsetime;
    @JsonProperty("metadata")
    private Object metadata;
    @JsonProperty("response")
    private HashMap<String, String> response;
    @JsonProperty("errors")
    private ArrayList<RsError> errors;
}
