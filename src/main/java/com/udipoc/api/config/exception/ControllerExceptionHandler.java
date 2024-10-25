package com.udipoc.api.config.exception;

import com.udipoc.api.util.enums.ResponseCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@ResponseBody
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler{

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage handlerResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                new Date(),
                ResponseCode.RESOURCE_NOT_FOUND.getCode(),
                ex.getMessage(),
                request.getDescription(false));

        return message;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage globalException(Exception ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                new Date(),
                ResponseCode.INTERNAL_SERVER_ERROR.getCode(),
                ex.getMessage(),
                request.getDescription(true));

        return message;
    }

    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(value=HttpStatus.OK)
    public ErrorMessage invalidaDataException(InvalidDataException ex, WebRequest request) {

        ResponseCode respCode=ResponseCode.valueOf(ex.getMessage());
        ErrorMessage message = new ErrorMessage(
                new Date(),
                respCode.getCode(),
                respCode.getDescription(),
                request.getDescription(false));

        return message;
    }

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(value=HttpStatus.EXPECTATION_FAILED)
    public ErrorMessage apiException(ApiException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                new Date(),
                ResponseCode.GENERAL_EXCEPTION.getCode(),
                ex.getMessage(),
                request.getDescription(false));

        return message;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getAllErrors().forEach((error) ->{
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put("message", message);
        });
        errors.put("timestamp",LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss'Z'")));
        errors.put("description",request.getDescription(false));
        return new ResponseEntity<Object>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ErrorMessage handleException(NullPointerException ex ,HttpHeaders headers, HttpStatus status, WebRequest request)
    {
        ErrorMessage message = new ErrorMessage(
                new Date(),
                ResponseCode.GENERAL_EXCEPTION.getCode(),
                ex.getMessage(),
                request.getDescription(false));

        return message;
    }

    @ExceptionHandler(value = {DeviceNotValidException.class})
    @ResponseStatus(value=HttpStatus.OK)
    public ErrorMessage handleDeviceNotValidException(DeviceNotValidException deviceNotValidException,WebRequest request)
    {
        ResponseCode respCode=ResponseCode.valueOf(deviceNotValidException.getMessage());
        ErrorMessage message = new ErrorMessage(
                new Date(),
                respCode.getCode(),
                respCode.getDescription(),
                request.getDescription(false));

        return message;
    }

}
