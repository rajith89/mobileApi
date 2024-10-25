package com.udipoc.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class MobOtpValidateRequest {
    @NotNull(message = "Invalid UIN")
    @NotEmpty(message = "Invalid UIN")
    @Size(min = 10, max = 10, message = "Invalid UIN")
    @JsonProperty("uin")
    private String uin;
    @NotNull(message = "Invalid otp")
    @NotEmpty(message = "Invalid otp")
    @Size(min = 4, max = 6, message = "Invalid otp")
    @JsonProperty("otp")
    private String otp;
    @NotNull(message = "Invalid deviceId")
    @NotEmpty(message = "Invalid deviceId")
    @Size(min = 4, message = "Invalid deviceId")
    private String deviceId;
}
