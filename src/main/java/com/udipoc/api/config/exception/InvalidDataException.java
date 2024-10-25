package com.udipoc.api.config.exception;

import com.udipoc.api.util.enums.ResponseCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class InvalidDataException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidDataException(ResponseCode responseCode) {
        super(responseCode.name());
        log.error("InvalidDataException : " +responseCode.getDescription(), this);
    }
}
