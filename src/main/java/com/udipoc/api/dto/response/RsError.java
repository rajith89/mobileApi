package com.udipoc.api.dto.response;

import lombok.Data;

@Data
public class RsError {
    private String errorCode;
    private String errorMessage;
    private String message;
}
