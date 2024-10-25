package com.udipoc.api.util.enums;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class ServiceEndpoint {
    @Value("${service-endpoints.base-endpoint}")
    private String BaseEndpoint;

    @Value("${service-endpoints.request.resident-service-auth-endpoint}")
    private String RequestResidentServiceAuthEndpoint;

    @Value("${service-endpoints.request.otp-endpoint}")
    private String RequestOtpEndpoint;

    @Value("${service-endpoints.request.otp-validate-endpoint}")
    private String RequestOtpValidateEndpoint;

    @Value("${service-endpoints.request.resident-info-endpoint}")
    private String RequestResidentInfoEndpoint;

    @Value("${service-endpoints.request.resident-auth-mode-lock-endpoint}")
    private String RequestResidentAuthModeLockEndpoint;

    @Value("${service-endpoints.request.resident-auth-mode-unlock-endpoint}")
    private String RequestResidentAuthModeUnlockEndpoint;

    @Value("${service-endpoints.request.auth-history-endpoint}")
    private String RequestAuthHistoryEndpoint;
}
