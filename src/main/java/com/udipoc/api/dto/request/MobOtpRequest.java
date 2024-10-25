package com.udipoc.api.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class MobOtpRequest {

    @NotNull(message = "Invalid UIN")
    @NotEmpty(message = "Invalid UIN")
    @Size(min = 10, max = 10, message = "Invalid UIN")
    private String uin;

    @NotNull(message = "Invalid deviceId")
    @NotEmpty(message = "Invalid deviceId")
    @Size(min = 4, message = "Invalid deviceId")
    private String deviceId;
}
