package com.develop25.trendit.dto;

import java.util.List;

/**
 * Expo Push API의 응답 구조입니다.
 */
public record ExpoReceiptResponseDto(
        List<ReceiptData> data
) {
    /**
     * 배치 요청에 포함된 각 알림의 결과를 담는 구조입니다.
     */
    public record ReceiptData(
            String status,      // 발송 상태 ("ok" 또는 "error") [cite: 306, 313]
            String id,          // 성공 시 Expo 영수증 ID [cite: 309]
            String message,     // 오류 시 메시지 [cite: 314]
            Details details     // 오류 상세 정보 (특히 DeviceNotRegistered) [cite: 316]
    ) {}

    /**
     * 오류 상세 정보 구조입니다.
     */
    public record Details(
            String error        // 오류 코드 (예: "DeviceNotRegistered") [cite: 317]
    ) {}
}
