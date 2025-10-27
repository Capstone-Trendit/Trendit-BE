package com.develop25.trendit.controller;

import com.develop25.trendit.domain.NotificationSetting;
import com.develop25.trendit.domain.PushToken;
import com.develop25.trendit.dto.*;
import com.develop25.trendit.exception.NotFoundException;
import com.develop25.trendit.service.NotificationApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationApiService notificationApiService;

    // 시간 형식 포매터 (HH:mm:ss -> HH:mm 변환용)
    private static final DateTimeFormatter TIME_FORMATTER_HHMM = DateTimeFormatter.ofPattern("HH:mm");

    // 사용자 인증 정보를 헤더에서 추출한다고 가정합니다.
    // 실제 구현에서는 Spring Security를 사용해야 합니다.
    private UUID getAuthenticatedUserId(String authorizationHeader) {
        // 임시 로직: 실제로는 JWT 토큰을 디코딩하여 userId를 추출해야 합니다.
        // 현재는 요청 본문에 userId가 포함되어 있다고 가정하고 그대로 사용합니다.
        // return extractedUserId;
        return null;
    }

    /**
     * 1. 푸시 토큰 등록/업데이트
     * [cite_start]Endpoint: POST /api/notifications/register [cite: 128]
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<PushTokenResponse>> registerPushToken(
            @RequestBody PushTokenRegisterRequest request) {

        try {
            // Service 호출하여 토큰 등록 또는 업데이트
            PushToken savedToken = notificationApiService.registerPushToken(
                    request.userId(),
                    request.pushToken(),
                    request.platform()
            );

            // 응답 DTO 변환
            PushTokenResponse responseData = new PushTokenResponse(
                    savedToken.getId(),
                    savedToken.getUserId(),
                    savedToken.getPushToken(),
                    savedToken.getPlatform(),
                    savedToken.getCreatedAt(),
                    savedToken.getUpdatedAt()
            );

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "푸시 토큰이 등록되었습니다.",
                    responseData
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        }
    }

    /**
     * 2. 알림 설정 저장/업데이트
     * [cite_start]Endpoint: PUT /api/notifications/settings [cite: 166]
     */
    @PutMapping("/settings")
    public ResponseEntity<ApiResponse<NotificationSettingResponse>> saveNotificationSettings(
            @RequestBody NotificationSettingSaveRequest request) {

        try {
            // Service 호출하여 알림 설정 저장 또는 업데이트
            NotificationSetting savedSetting = notificationApiService.saveNotificationSettings(
                    request.userId(),
                    request.enabled(),
                    request.notificationTime()
            );

            // 응답 DTO 변환
            NotificationSettingResponse responseData = new NotificationSettingResponse(
                    savedSetting.getId(),
                    savedSetting.getUserId(),
                    savedSetting.isEnabled(),
                    savedSetting.getNotificationTime().toString(), // TIME 객체를 문자열로
                    savedSetting.getTimezone(),
                    savedSetting.getCreatedAt(),
                    savedSetting.getUpdatedAt()
            );

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "알림 설정이 저장되었습니다.",
            responseData
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        }
    }

    /**
     * 3. 알림 설정 조회
     * [cite_start]Endpoint: GET /api/notifications/settings/:userld [cite: 206]
     */
    @GetMapping("/settings/{userId}")
    public ResponseEntity<ApiResponse<NotificationSettingResponse>> getNotificationSettings(
            @PathVariable String userId) {

        try {
            NotificationSetting setting = notificationApiService.getNotificationSettings(userId);

            String notificationTimeHHMM = setting.getNotificationTime().format(TIME_FORMATTER_HHMM);

            NotificationSettingResponse responseData = new NotificationSettingResponse(
                    setting.getId(),
                    setting.getUserId(),
                    setting.isEnabled(),
                    notificationTimeHHMM,
                    setting.getTimezone(),
                    setting.getCreatedAt(),
                    setting.getUpdatedAt()
            );

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    null,
                    responseData
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        }
    }
}