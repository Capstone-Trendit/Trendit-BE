package com.develop25.trendit.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification_settings")
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // 기본 키

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId; // 사용자 ID (외래 키, UNIQUE)

    @Column(name = "enabled", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean enabled = true; // 알림 활성화 여부

    @Column(name = "notification_time", columnDefinition = "TIME DEFAULT '09:00:00'")
    private LocalTime notificationTime = LocalTime.of(9, 0, 0); // 알림 발송 시간

    @Column(name = "timezone", length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'Asia/Seoul'")
    private String timezone = "Asia/Seoul"; // 사용자의 타임존

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now(); // 생성 시간

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now(); // 수정 시간

}
