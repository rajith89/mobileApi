package com.udipoc.api.controller;

import com.udipoc.api.config.exception.ApiException;
import com.udipoc.api.config.exception.InvalidDataException;
import com.udipoc.api.dto.endpoint.APIResponse;
import com.udipoc.api.dto.request.AuthHistoryRequest;
import com.udipoc.api.dto.request.MobAuthModeRequest;
import com.udipoc.api.dto.response.ResidentInfoResponse;
import com.udipoc.api.dto.response.RsAuthModeResponse;
import com.udipoc.api.service.AuthService;
import com.udipoc.api.service.ResidentInfoService;
import com.udipoc.api.service.ResidentService;
import com.udipoc.api.util.enums.ResponseCode;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/resident")
@CrossOrigin
@Slf4j
public class ResidentServiceController {

    @Autowired
    private  ResidentService residentService;

    @Autowired
    private ResidentInfoService residentInfoService;

    @Autowired
    private AuthService authService;

    public ResidentServiceController() {
    }

    @ApiOperation(value = "Enable lock in resident auth modes", response = RsAuthModeResponse.class)
    @PostMapping(value = "/auth-lock")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> lockAuthmode(@Valid @RequestBody MobAuthModeRequest mobAuthModeRequest) throws ApiException, InvalidDataException {
       return ResponseEntity.ok(new APIResponse<>(ResponseCode.RESIDENT_AUTH_LOCK_SUCCES,residentService.lockAuthModes(mobAuthModeRequest)));
    }

    @ApiOperation(value = "Disable lock in resident auth modes", response = RsAuthModeResponse.class)
    @PostMapping(value = "/auth-unlock")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> unlockAuthmode(@Valid @RequestBody MobAuthModeRequest mobAuthModeRequest, HttpSession session) throws ApiException, InvalidDataException {
        return ResponseEntity.ok(new APIResponse<>(ResponseCode.RESIDENT_AUTH_UNLOCK_SUCCES,residentService.unlockAuthModes(mobAuthModeRequest)));
    }

    @ApiOperation(value = "Get resident info", response = ResidentInfoResponse.class)
    @GetMapping(value = "/info")
    public ResponseEntity<?> getInfo(@RequestParam String uin) throws ApiException, InvalidDataException {
        return ResponseEntity.ok(new APIResponse<>(ResponseCode.SUCCESS_RESIDENT_INFO,residentInfoService.getDemographicData(uin)));
    }

    @ApiOperation(value = "Retrieve Resident Auth history", response = RsAuthModeResponse.class)
    @PostMapping(path = "/auth-history", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> authHistory(@Valid @RequestBody AuthHistoryRequest authHistoryRequest) throws ApiException,InvalidDataException {
        return ResponseEntity.ok(new APIResponse<>(ResponseCode.SUCCESS_AUTH_HISTORY, residentService.getAuthHistory(authHistoryRequest)));
    }

    @ApiOperation(value = "Unblock user for accessing resident mobile apis", response = APIResponse.class)
    @GetMapping(value = "/unblock")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> userUnblock(@Valid @RequestParam String deviceId,@RequestParam String uin) throws ApiException, InvalidDataException {
        return ResponseEntity.ok(new APIResponse<>(ResponseCode.SUCCESS_USER_UNBLOCK, authService.userUnblock(deviceId,uin)));
    }
}



