package com.udipoc.api.config.exception;

import com.udipoc.api.util.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeviceNotValidException extends Exception{
    private static final long serialVersionUID = 1L;

    public DeviceNotValidException(ResponseCode resp) {
        super(resp.name());
        log.error("DeviceNotValidException : "+resp.getDescription());
    }

    public DeviceNotValidException(String message, Throwable cause) {
        super(message, cause);
    }
}
