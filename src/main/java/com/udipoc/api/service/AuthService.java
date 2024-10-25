package com.udipoc.api.service;

import com.udipoc.api.config.exception.ApiException;
import com.udipoc.api.config.exception.DeviceNotValidException;
import com.udipoc.api.config.exception.InvalidDataException;
import com.udipoc.api.dto.request.MobLoginRequest;
import com.udipoc.api.dto.request.MobOtpRequest;
import com.udipoc.api.dto.request.MobOtpValidateRequest;
import com.udipoc.api.dto.response.MobAuthResponse;
import com.udipoc.api.dto.response.MobAuthUnblockResponse;
import com.udipoc.api.dto.response.MobOtpResponse;

public interface AuthService {

    MobLoginRequest validateOtpRequest(MobOtpValidateRequest mobOtpValidateRequest) throws ApiException, InvalidDataException;

    MobOtpResponse requestOtp(MobOtpRequest mobOtpRequest) throws ApiException, InvalidDataException, DeviceNotValidException;

    MobAuthResponse userAuthenticate(MobLoginRequest mobLoginRequest) throws ApiException;

    MobAuthUnblockResponse userUnblock(String deviceId,String uin) throws ApiException, InvalidDataException;
}
