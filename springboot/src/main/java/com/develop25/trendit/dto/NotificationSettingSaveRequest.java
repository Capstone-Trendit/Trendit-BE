package com.develop25.trendit.dto;

// PUT /api/notifications/settings 요청 시
public record NotificationSettingSaveRequest(
        String userId,
        Boolean enabled,
        String notificationTime
) {}