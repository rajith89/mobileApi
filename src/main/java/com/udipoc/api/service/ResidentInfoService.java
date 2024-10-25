package com.udipoc.api.service;
import com.udipoc.api.config.exception.ApiException;
import com.udipoc.api.config.exception.InvalidDataException;
import com.udipoc.api.dto.response.ResidentInfoResponse;

public interface ResidentInfoService {

    public ResidentInfoResponse getDemographicData(String uin) throws ApiException, InvalidDataException;
}
