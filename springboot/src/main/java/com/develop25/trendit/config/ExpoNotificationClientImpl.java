package com.develop25.trendit.config;

import com.develop25.trendit.dto.ExpoReceiptResponseDto;
import com.develop25.trendit.dto.PushNotificationDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * ExpoNotificationClient 인터페이스의 WebClient 구현체입니다.
 */
@Component
public class ExpoNotificationClientImpl implements ExpoNotificationClient {

    private final WebClient expoWebClient;

    // WebClientConfig에서 정의한 'expoWebClient' Bean을 주입받습니다.
    public ExpoNotificationClientImpl(@Qualifier("expoWebClient") WebClient expoWebClient) {
        this.expoWebClient = expoWebClient;
    }

    @Override
    public ExpoReceiptResponseDto send(List<PushNotificationDto> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            // 빈 요청은 보내지 않음
            return new ExpoReceiptResponseDto(List.of());
        }

        try {

            return expoWebClient.post()
                    .uri("") // baseUrl이 이미 전체 엔드포인트이므로 URI 경로는 빈 문자열
                    .bodyValue(notifications) // DTO 리스트가 JSON 배열로 변환되어 전송됨 (다중 알림 권장 [cite: 286])
                    .retrieve()

                    // 4xx 또는 5xx 오류 처리: HTTP 상태 코드가 에러인 경우 예외 발생
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse ->
                            clientResponse.createException().flatMap(e -> {
                                System.err.println("Expo API HTTP 오류: " + e.getMessage());
                                return Mono.error(new RuntimeException("Expo API HTTP 통신 실패", e));
                            })
                    )

                    .bodyToMono(ExpoReceiptResponseDto.class) // 응답 본문을 DTO로 변환
                    .block(); // 동기적으로 결과를 기다림 (스케줄러 환경에서는 일반적)

        } catch (WebClientResponseException e) {
            // HTTP 상태 코드 기반 예외 처리
            System.err.println("Expo API HTTP 오류 (" + e.getStatusCode() + "): " + e.getResponseBodyAsString());
            throw new RuntimeException("Expo API HTTP 통신 실패", e);
        } catch (Exception e) {
            // 기타 네트워크 오류 또는 파싱 오류 처리
            System.err.println("Expo API 요청 중 알 수 없는 오류 발생: " + e.getMessage());
            throw new RuntimeException("Expo API 요청 중 오류 발생", e);
        }
    }
}
