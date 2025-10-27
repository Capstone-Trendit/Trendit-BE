package com.develop25.trendit.dto;

import java.util.List;

/**
 * 알림의 data 페이로드 (JSONB) 구조입니다.
 * 앱에서 라우팅 및 상세 정보를 위해 사용됩니다.
 */
public record DataPayloadDto(
        String type,                    // 알림 타입 (예: "top5_products")
        List<TopProductStatDto> products // TOP5 상품 목록 상세 정보
) {}