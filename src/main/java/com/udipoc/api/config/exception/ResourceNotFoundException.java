package com.udipoc.api.config.exception;

import com.udipoc.api.util.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResourceNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(ResponseCode resp) {
        super(resp.getDescription());
        log.error("ResourceNotFoundException : "+resp.getDescription());
    }
}
