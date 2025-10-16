package com.develop25.trendit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "naver")
public record NaverApiProperties(
        String clientId,
        String clientSecret,
        String baseUrl,
        Integer timeoutMs
) {}