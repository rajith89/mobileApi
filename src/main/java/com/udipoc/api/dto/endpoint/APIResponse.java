package com.udipoc.api.dto.endpoint;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.udipoc.api.util.enums.ResponseCode;
import lombok.Data;
import lombok.NonNull;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse<T> {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'")
    @JsonProperty("timestamp")
    private Date timestamp;
    @JsonProperty("status")
    private int status;
    @JsonProperty("message")
    private String message;
    @JsonProperty("body")
    private T body;

    public APIResponse(@NonNull ResponseCode header) {
        this.timestamp = new Date();
        this.status = header.getCode();
        this.message = header.getDescription();
    }

    public APIResponse(@NonNull ResponseCode header, T body) {
        this(header);
        this.body = body;
    }
}
