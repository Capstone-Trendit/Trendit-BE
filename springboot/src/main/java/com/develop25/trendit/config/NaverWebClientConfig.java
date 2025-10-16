package com.develop25.trendit.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.*;

@Configuration
@EnableConfigurationProperties(NaverApiProperties.class) //application.properties 스캔을 위한 부분
public class NaverWebClientConfig {
    @Bean
    WebClient naverWebClient(NaverApiProperties props) {
        return WebClient.builder()
                .baseUrl(props.baseUrl())
                .defaultHeader("X-Naver-Client-Id", props.clientId())
                .defaultHeader("X-Naver-Client-Secret", props.clientSecret())
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build();
    }
}

