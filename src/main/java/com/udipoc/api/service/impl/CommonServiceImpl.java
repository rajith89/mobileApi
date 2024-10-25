package com.udipoc.api.service.impl;

import com.udipoc.api.config.exception.ApiException;
import com.udipoc.api.config.redistcache.RedisServiceTokenRepository;
import com.udipoc.api.dto.endpoint.ServiceAuthRequest;
import com.udipoc.api.dto.request.ServiceCredential;
import com.udipoc.api.entity.ServiceToken;
import com.udipoc.api.entity.UserSession;
import com.udipoc.api.repository.UserSessionRepository;
import com.udipoc.api.service.CommonService;
import com.udipoc.api.util.CustomLogoutHandler;
import com.udipoc.api.util.DateUtils;
import com.udipoc.api.util.enums.ServiceEndpoint;
import com.udipoc.api.util.enums.ServiceName;
import com.udipoc.api.util.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class CommonServiceImpl implements CommonService {

    private final RedisServiceTokenRepository redisServiceTokenRepository;
    private final Environment environment;
    private final RestTemplate restTemplate;
    private final UserSessionRepository userSessionRepository;

    @Autowired
    public CommonServiceImpl(Environment environment, RedisServiceTokenRepository redisServiceTokenRepository, RestTemplate restTemplate, UserSessionRepository userSessionRepository) {
        this.redisServiceTokenRepository = redisServiceTokenRepository;
        this.environment = environment;
        this.restTemplate = restTemplate;
        this.userSessionRepository = userSessionRepository;
    }

    @Autowired
    ServiceEndpoint serviceEndpoint;

    @Transactional
    @Override
    public HttpHeaders getClientServiceAuthentication(ServiceName serviceName) throws ApiException {
        HttpHeaders headers = new HttpHeaders();
        String serviceAuthtoken = "";
        ServiceAuthRequest serviceAuthRequest = new ServiceAuthRequest();

        if (serviceName.equals(ServiceName.RESIDENT_SERVICE)) {
            ServiceToken serviceToken = getServiceToken(ServiceName.RESIDENT_SERVICE);
            if (serviceToken != null) {
                serviceAuthtoken = serviceToken.getToken();
            } else {
                ServiceCredential serviceCredential = new ServiceCredential();
                serviceCredential.setAppId(environment.getProperty("resident.token.request.appid"));
                serviceCredential.setClientId(environment.getProperty("resident.token.request.clientId"));
                serviceCredential.setSecretKey(environment.getProperty("resident.token.request.secretKey"));

                serviceAuthRequest.setRequest(serviceCredential);
                serviceAuthRequest.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
                serviceAuthtoken = getServiceAuthToken(serviceAuthRequest, ServiceName.RESIDENT_SERVICE);
            }
        } else if (serviceName.equals(ServiceName.IDENTITY_SERVICE)) {
            ServiceToken serviceToken = getServiceToken(ServiceName.IDENTITY_SERVICE);
            if (serviceToken != null) {
                serviceAuthtoken = serviceToken.getToken();
            } else {
                ServiceCredential serviceCredential = new ServiceCredential();
                serviceCredential.setAppId(environment.getProperty("identity.token.request.appid"));
                serviceCredential.setClientId(environment.getProperty("identity.token.request.clientId"));
                serviceCredential.setSecretKey(environment.getProperty("identity.token.request.secretKey"));

                serviceAuthRequest.setRequest(serviceCredential);
                serviceAuthRequest.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
                serviceAuthtoken = getServiceAuthToken(serviceAuthRequest, ServiceName.IDENTITY_SERVICE);
            }
        }
        headers.add("Cookie", serviceAuthtoken);
        return headers;
    }

    @Transactional
    @Cacheable(value = "serviceToken", key = "#serviceName", unless = "#result.id < 1")
    private ServiceToken getServiceToken(ServiceName serviceName) {
        return redisServiceTokenRepository.findByServiceName(serviceName);
    }

    @Transactional
    @CacheEvict(value = "serviceToken", key = "#serviceToken")
    private void removeServiceToken(ServiceName serviceName) {
        redisServiceTokenRepository.delete(serviceName);
    }

    @Transactional
    @CachePut(value = "serviceToken", key = "#serviceToken", unless = "#result.id < 10")
    private String getServiceAuthToken(ServiceAuthRequest serviceAuthRequest, ServiceName serviceName) {
        String authServiceToken = "";
        ServiceToken serviceToken = new ServiceToken();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ServiceAuthRequest> entity = new HttpEntity<ServiceAuthRequest>(serviceAuthRequest, httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(serviceEndpoint.getRequestResidentServiceAuthEndpoint(), HttpMethod.POST, entity, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String cookie = responseEntity.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
            if (!Objects.requireNonNull(cookie).isEmpty()) {
                authServiceToken = cookie;
                serviceToken.setId(1L);
                serviceToken.setServiceName(serviceName);
                serviceToken.setToken(authServiceToken);
                serviceToken.setGenerateTimestamp(new Date());
                redisServiceTokenRepository.save(serviceToken);
            }
        }
        return authServiceToken;
    }

    @Transactional
    @Override
    public void userSessionLogOut(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ApiException {
        String username = String.valueOf(request.getAttribute("username"));
        String token = String.valueOf(request.getAttribute("bearer-access-token"));
        Optional<UserSession> loginUser = userSessionRepository.findUserIsLoginOrNot(username, Status.ACTIVE, Status.ACTIVE, token);
        if (loginUser.isPresent()) {
            loginUser.get().setLoginStatus(Status.INACTIVE);
            loginUser.get().setToken("");
            userSessionRepository.save(loginUser.get());
        }
        new CustomLogoutHandler().logout(request, response, authentication);
    }
}
