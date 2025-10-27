package com.develop25.trendit.dto;

import java.time.Instant;
import java.util.UUID;

// 알림 설정 응답에 사용될 DTO
public record NotificationSettingResponse(
        UUID id,
        String userId,
        boolean enabled,
        String notificationTime,
        String timezone,
        Instant createdAt,
        Instant updatedAt
) {}