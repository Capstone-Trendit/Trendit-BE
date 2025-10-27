package com.develop25.trendit.repository;

import com.develop25.trendit.domain.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, UUID> {
    // user_id로 설정 조회 (user_id는 UNIQUE 제약 조건이 있으므로 Optional)
    Optional<NotificationSetting> findByUserId(String userId);
}
