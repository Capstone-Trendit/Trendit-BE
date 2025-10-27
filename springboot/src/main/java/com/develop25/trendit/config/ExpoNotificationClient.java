package com.develop25.trendit.config;

import com.develop25.trendit.dto.ExpoReceiptResponseDto;
import com.develop25.trendit.dto.PushNotificationDto;

import java.util.List;

/**
 * Expo Push API와의 통신을 위한 클라이언트 인터페이스.
 */
public interface ExpoNotificationClient {

    /**
     * 알림 목록을 Expo API로 전송하고 결과를 반환합니다.
     * @param notifications 전송할 PushNotificationDto 목록 (최대 100개)
     * @return Expo API 응답 (영수증 ID 및 오류 정보 포함)
     */
    ExpoReceiptResponseDto send(List<PushNotificationDto> notifications);
}