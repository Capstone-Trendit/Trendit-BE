package com.develop25.trendit.repository;

import com.develop25.trendit.domain.PushToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PushTokenRepository extends JpaRepository<PushToken, UUID> {
    // user_id와 push_token으로 토큰 조회
    Optional<PushToken> findByUserIdAndPushToken(String userId, String pushToken);

    // 특정 사용자 ID를 가진 활성 토큰 목록 조회
    // 여러 디바이스를 지원하기 위해 List로 받음
    List<PushToken> findAllByUserIdAndIsActiveTrue(String userId);
}