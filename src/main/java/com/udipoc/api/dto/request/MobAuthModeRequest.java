package com.udipoc.api.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class MobAuthModeRequest {

    @NotNull(message = "Invalid UIN")
    @NotEmpty(message = "Invalid UIN")
    @Size(min = 10,max = 10, message = "Invalid UIN")
    private String uin;

    @NotNull(message = "Invalid transactionId")
    @NotEmpty(message = "Invalid transactionId")
    @Size(min = 10, message = "Invalid transactionId")
    private String transactionId;

    @NotNull(message = "Invalid otp")
    @NotEmpty(message = "Invalid otp")
    @Size(min = 4, max=6,message = "Invalid otp")
    private String otp;

    @NotNull(message = "Invalid authType")
    @NotEmpty(message = "Invalid authType")
    private List<String> authType;


}
