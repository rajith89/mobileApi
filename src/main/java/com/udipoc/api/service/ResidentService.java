package com.udipoc.api.service;

import com.udipoc.api.config.exception.ApiException;
import com.udipoc.api.config.exception.InvalidDataException;
import com.udipoc.api.dto.request.AuthHistoryRequest;
import com.udipoc.api.dto.request.MobAuthModeRequest;
import com.udipoc.api.dto.response.AuthHistoryResponse;
import com.udipoc.api.dto.response.MobileAuthModeResponse;
import com.udipoc.api.dto.response.RsAuthModeResponse;

import java.util.List;

public interface ResidentService {

    MobileAuthModeResponse lockAuthModes(MobAuthModeRequest request) throws ApiException,InvalidDataException;
    MobileAuthModeResponse unlockAuthModes(MobAuthModeRequest request) throws ApiException, InvalidDataException;
    List<AuthHistoryResponse> getAuthHistory(AuthHistoryRequest authHistoryRequest) throws ApiException, InvalidDataException;
}
