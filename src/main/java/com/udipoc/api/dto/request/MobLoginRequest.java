package com.udipoc.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MobLoginRequest {
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;
}
