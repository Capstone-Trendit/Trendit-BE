package com.develop25.trendit.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Expo Push API로 전송되는 단일 알림 객체의 구조입니다.
 */

public record PushNotificationDto(
        String to,          // 필수: ExponentPushToken[...] 형태의 푸시 토큰 [cite: 265]
        String title,       // 알림 제목 (최대 50자 권장) [cite: 266, 375]
        String body,        // 알림 본문 (최대 178자 권장, 줄바꿈(\n) 사용 가능) [cite: 267, 382, 384]
        DataPayloadDto data, // 알림 클릭 시 앱에서 사용할 JSON 데이터
        String sound,       // 알림 소리 (예: "default") [cite: 282]
        Integer badge,      // 앱 아이콘에 표시될 뱃지 수 [cite: 283]
        String priority,    // 알림 중요도 (예: "high") [cite: 284]
        String channelId    // Android 알림 채널 ID (예: "default") [cite: 285]
) {}
