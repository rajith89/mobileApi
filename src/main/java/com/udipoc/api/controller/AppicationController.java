package com.udipoc.api.controller;

import com.udipoc.api.config.exception.ApiException;
import com.udipoc.api.dto.endpoint.APIResponse;
import com.udipoc.api.service.CommonService;
import com.udipoc.api.util.enums.ResponseCode;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/app")
@Slf4j
public class AppicationController {

    private final CommonService commonService;

    public AppicationController(CommonService commonService) {
        this.commonService = commonService;
    }

    @ApiOperation(value = "Logout", response = APIResponse.class)
    @GetMapping(value = "/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) throws ApiException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            commonService.userSessionLogOut(request, response, auth);
            log.info("Logout Success.");
            return ResponseEntity.ok(new APIResponse<>(ResponseCode.SUCCESS_LOGOUT_REQUEST));
        } else {
            log.info("User Already Logout.");
            return ResponseEntity.ok(new APIResponse<>(ResponseCode.SUCCESS_ALREADY_LOGOUT));
        }
    }
}
