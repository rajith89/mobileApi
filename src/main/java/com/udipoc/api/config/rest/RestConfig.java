package com.udipoc.api.config.rest;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestConfig {

    private static final int TIMEOUT = 30;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(TIMEOUT))
                .setReadTimeout(Duration.ofSeconds(TIMEOUT))
                .build();
    }
}
