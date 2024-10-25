package com.udipoc.api.config.exception;

import com.udipoc.api.util.enums.ResponseCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
@Slf4j
@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class ApiException extends RuntimeException {

    public ApiException(ResponseCode responseCode) {
        super(responseCode.getDescription());
        log.error(responseCode.getDescription(), this);
    }
}
