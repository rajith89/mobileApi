package com.udipoc.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MobOtpResponse implements Serializable {

    private static final long serialVersionUID = 7156526077883281623L;

    @JsonProperty("mobile")
    private String mobile;
    @JsonProperty("transactionId")
    private String transactionId;
}
