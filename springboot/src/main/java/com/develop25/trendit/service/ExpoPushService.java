package com.develop25.trendit.service;

import com.develop25.trendit.config.ExpoNotificationClient;
import com.develop25.trendit.domain.PushToken;
import com.develop25.trendit.dto.ExpoReceiptResponseDto;
import com.develop25.trendit.dto.PushNotificationDto;
import com.develop25.trendit.repository.PushTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpoPushService {

    private final ExpoNotificationClient expoClient;   // WebClient 등을 사용한 가상의 클라이언트
    private final PushTokenRepository pushTokenRepository;

    /**
     * 푸시 알림 배치 발송 [cite: 286]
     * Expo API 호출 및 응답 처리 (로깅, 토큰 비활성화)
     */
    @Transactional
    public void sendPushNotifications(List<PushNotificationDto> notifications) {
        if (notifications.isEmpty()) return;

        try {
            // Expo Push API 호출 (최대 100개까지 한 번에 발송 가능)
            ExpoReceiptResponseDto response = expoClient.send(notifications);

            List<ExpoReceiptResponseDto.ReceiptData> results = response.data();
            for (int i = 0; i < results.size(); i++) {
                ExpoReceiptResponseDto.ReceiptData result = results.get(i);
                PushNotificationDto notification = notifications.get(i);

                // 1. 발송 결과 로깅
                // logNotification(notification, result);

                if ("error".equals(result.status())) {
                    // 2. 오류 처리: DeviceNotRegistered 오류인 경우 토큰 비활성화
                    if (result.details() != null && "DeviceNotRegistered".equals(result.details().error())) {
                        deactivatePushToken(notification.to()); // 토큰 비활성화 메서드 호출
                    }
                    // 실패 로그
                    // logNotification(notification, result.message(), "failed");
                } else {
                    // 성공 로그
                    // logNotification(notification, result.id(), "sent");
                }
            }
        } catch (Exception e) {
            // API 통신 실패 등 예외 처리
            System.err.println("푸시 알림 발송 실패: " + e.getMessage());
            // 모든 알림에 대해 'failed' 로깅 처리 로직 추가
        }
    }

    /**
     * 유효하지 않은 토큰 비활성화 [cite: 610]
     */
    @Transactional
    public void deactivatePushToken(String pushToken) {
        pushTokenRepository.findAll().stream()
                .filter(token -> token.getPushToken().equals(pushToken))
                .forEach(PushToken::deactivate);
    }
}