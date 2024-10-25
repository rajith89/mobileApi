package com.udipoc.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MobAuthResponse implements Serializable {

    private static final long serialVersionUID = 7156526077883281623L;

    @JsonProperty("username")
    private String username;
    @JsonProperty("token")
    private String token;
    @JsonProperty("type")
    private String type;
}
