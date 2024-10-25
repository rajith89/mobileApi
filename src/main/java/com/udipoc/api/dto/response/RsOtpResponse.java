package com.udipoc.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

@Data
public class RsOtpResponse implements Serializable {

    private static final long serialVersionUID = 7156526077883281623L;

    @JsonProperty("id")
    private String id;
    @JsonProperty("version")
    private String version;
    @JsonProperty("transactionID")
    private String transactionID;
    @JsonProperty("responseTime")
    private String responseTime;
    @JsonProperty("errors")
    private ArrayList<RsError> errors;
    @JsonProperty("response")
    private HashMap<String, String> response;
    @JsonProperty("metadata")
    private Object metadata;
}
