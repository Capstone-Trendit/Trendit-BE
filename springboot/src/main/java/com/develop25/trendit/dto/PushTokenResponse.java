package com.develop25.trendit.dto;

import java.time.Instant;
import java.util.UUID;

// 푸시 토큰 등록 응답에 사용될 DTO
public record PushTokenResponse(
        UUID id,
        String userId,
        String pushToken,
        String platform,
        Instant createdAt,
        Instant updatedAt
) {}
