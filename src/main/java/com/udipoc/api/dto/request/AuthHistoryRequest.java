package com.udipoc.api.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthHistoryRequest {
   /* @NotEmpty(message = "Invalid TransactionID")
    @NotNull(message = "Invalid TransactionID")
    @JsonProperty("transactionID")
    private String transactionId;*/

    @NotEmpty(message = "Invalid UIN")
    @NotNull(message = "Invalid UIN")
    @JsonProperty("individualId")
    private String individualId;

    /*@NotEmpty(message = "Invalid OTP")
    @NotNull(message = "Invalid OTP")
    @JsonProperty("otp")
    private String otp;*/
}
