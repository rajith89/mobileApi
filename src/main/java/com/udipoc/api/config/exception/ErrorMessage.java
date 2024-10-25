package com.udipoc.api.config.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ErrorMessage {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'")
    private Date timestamp;
    private int errorCode;
    private String message;
    private String description;

    public ErrorMessage(Date timestamp,int errorCode, String message, String description) {
        this.timestamp = timestamp;
        this.errorCode=errorCode;
        this.message = message;
        this.description = description;
    }
}
