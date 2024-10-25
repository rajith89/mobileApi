package com.udipoc.api.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthHistoryRequestBody {
    private String transactionID;
    private String individualId;
    private String individualIdType;
    private String otp;
}
