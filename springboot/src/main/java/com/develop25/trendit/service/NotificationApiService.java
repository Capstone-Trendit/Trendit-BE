package com.develop25.trendit.service;

import com.develop25.trendit.domain.NotificationSetting;
import com.develop25.trendit.domain.PushToken;
import com.develop25.trendit.exception.NotFoundException;
import com.develop25.trendit.repository.NotificationSettingRepository;
import com.develop25.trendit.repository.PushTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationApiService {

    private final PushTokenRepository pushTokenRepository;
    private final NotificationSettingRepository settingRepository;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * 푸시 토큰 등록 또는 업데이트 [cite: 127]
     */
    @Transactional
    public PushToken registerPushToken(String userId, String pushToken, String platform) {
        // 1. 토큰 유효성 검증 (여기서는 Expo 형식만 체크)
        if (!pushToken.startsWith("ExponentPushToken[")) {
            throw new IllegalArgumentException("유효하지 않은 푸시 토큰 형식입니다.");
        }

        // 2. 기존 토큰이 있는지 확인 [cite: 163]
        Optional<PushToken> existingToken = pushTokenRepository.findByUserIdAndPushToken(userId, pushToken);

        PushToken token;
        if (existingToken.isPresent()) {
            // 3. 기존 토큰이 있으면 업데이트 (플랫폼 변경 등)
            token = existingToken.get();
            token.setPlatform(platform);
            token.setActive(true); // 비활성화되어 있었다면 재활성화
        } else {
            // 3. 없으면 새로 생성
            token = new PushToken();
            token.setUserId(userId);
            token.setPushToken(pushToken);
            token.setPlatform(platform);
            token.setActive(true);
        }

        // 4. updatedAt 필드는 엔티티의 @PreUpdate로 자동 갱신
        return pushTokenRepository.save(token); // DB에 토큰 저장
    }

    /**
     * 알림 설정 저장 또는 업데이트 [cite: 165]
     */
    @Transactional
    public NotificationSetting saveNotificationSettings(String userId, boolean enabled, String notificationTimeStr) {
        // 1. 시간 형식 유효성 검증 (HH:mm)
        LocalTime notificationTime;
        try {
            notificationTime = LocalTime.parse(notificationTimeStr, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("유효하지 않은 시간 형식입니다. (HH:mm)");
        }

        // 2. 기존 설정이 있는지 확인
        Optional<NotificationSetting> existingSetting = settingRepository.findByUserId(userId);

        NotificationSetting setting;
        if (existingSetting.isPresent()) {
            // 3. 기존 설정이 있으면 업데이트
            setting = existingSetting.get();
            setting.setEnabled(enabled);
            setting.setNotificationTime(notificationTime);
        } else {
            // 3. 없으면 새로 생성
            setting = new NotificationSetting();
            setting.setUserId(userId);
            setting.setEnabled(enabled);
            setting.setNotificationTime(notificationTime);
            // Timezone은 기본값 'Asia/Seoul' 사용
        }

        // 4. updatedAt 필드는 엔티티의 @PreUpdate로 자동 갱신됨
        return settingRepository.save(setting); // DB에 설정 저장
    }

    /**
     * 알림 설정 조회
     */
    @Transactional(readOnly = true)
    public NotificationSetting getNotificationSettings(String userId) {
        // 1. 사용자 인증 확인 및 권한 검증 (여기선 인증된 userId 사용 전제)
        return settingRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("알림 설정을 찾을 수 없습니다."));
    }
}