package com.udipoc.api.controller;

import com.udipoc.api.config.exception.ApiException;
import com.udipoc.api.config.exception.DeviceNotValidException;
import com.udipoc.api.config.exception.InvalidDataException;
import com.udipoc.api.dto.endpoint.APIResponse;
import com.udipoc.api.dto.request.MobLoginRequest;
import com.udipoc.api.dto.request.MobOtpRequest;
import com.udipoc.api.dto.request.MobOtpValidateRequest;
import com.udipoc.api.dto.response.MobAuthResponse;
import com.udipoc.api.dto.response.MobOtpResponse;
import com.udipoc.api.service.AuthService;
import com.udipoc.api.util.enums.ResponseCode;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/auth")
@CrossOrigin
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping(value = "/run")
    public String let() {
        log.info("Hi from UDIPOC mobile API!! this is a info log");
        return "service running";
    }

    @ApiOperation(value = "Request OTP", response = APIResponse.class)
    @PostMapping(value = "/request-otp")
    public ResponseEntity<?> requestOtp(@Valid @RequestBody MobOtpRequest mobOtpRequest) throws ApiException, InvalidDataException, DeviceNotValidException {
        MobOtpResponse mobOtpResponse = authService.requestOtp(mobOtpRequest);
        return ResponseEntity.ok(new APIResponse<>(ResponseCode.SUCCESS_OTP_REQUEST, mobOtpResponse));
    }

    @ApiOperation(value = "Generate Auth Token & Validate OTP", response = APIResponse.class)
    @PostMapping(value = "/generate-token")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> authentication(@Valid @RequestBody MobOtpValidateRequest mobOtpValidateRequest) throws ApiException, InvalidDataException {
        MobLoginRequest mobLoginRequest = authService.validateOtpRequest(mobOtpValidateRequest);
        MobAuthResponse mobAuthResponse = authService.userAuthenticate(mobLoginRequest);
        return ResponseEntity.ok(new APIResponse<>(ResponseCode.SUCCESS_AUTH, mobAuthResponse));
    }
}
