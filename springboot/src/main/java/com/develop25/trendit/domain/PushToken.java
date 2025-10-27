package com.develop25.trendit.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "push_tokens", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "push_token"}) // user_id와 push_token의 조합은 고유해야 함
})
public class PushToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "push_token", nullable = false, length = 255)
    private String pushToken;

    @Column(name = "platform", nullable = false, length = 20)
    private String platform;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    // Getter, Setter, Constructors (생략)
    // is_active를 false로 변경하는 메서드 추가
    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public void deactivate() {
        this.isActive = false; // DeviceNotRegistered 오류 시 사용
    }
}