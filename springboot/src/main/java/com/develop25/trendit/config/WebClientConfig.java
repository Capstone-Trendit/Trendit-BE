package com.develop25.trendit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private static final String EXPO_PUSH_API_URL = "https://exp.host/--/api/v2/push/send"; // Expo 엔드포인트

    @Bean("expoWebClient") // 빈 이름을 명시하여 주입 가능하도록 함
    public WebClient expoWebClient() {
        return WebClient.builder()
                .baseUrl(EXPO_PUSH_API_URL)
                .defaultHeader("Content-Type", "application/json") // 필수 헤더
                .defaultHeader("Accept", "application/json") // 필수 헤더
                .defaultHeader("Accept-Encoding", "gzip, deflate") // 필수 헤더
                .build();
    }
}