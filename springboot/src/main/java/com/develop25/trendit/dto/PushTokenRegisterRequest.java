package com.develop25.trendit.dto;

// POST /api/notifications/register 요청 시
public record PushTokenRegisterRequest(
        String userId,
        String pushToken,
        String platform
) {}
