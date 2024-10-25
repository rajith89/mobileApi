package com.udipoc.api.service;

import com.udipoc.api.config.exception.ApiException;
import com.udipoc.api.util.enums.ServiceName;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface CommonService {

    HttpHeaders getClientServiceAuthentication(ServiceName serviceName) throws ApiException;

    void userSessionLogOut(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws ApiException;
}
