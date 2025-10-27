package com.develop25.trendit.task;

import com.develop25.trendit.domain.NotificationSetting;
import com.develop25.trendit.domain.PushToken;
import com.develop25.trendit.dto.DataPayloadDto;
import com.develop25.trendit.dto.PushNotificationDto;
import com.develop25.trendit.dto.TopProductStatDto;
import com.develop25.trendit.repository.NotificationSettingRepository;
import com.develop25.trendit.repository.PushTokenRepository;
import com.develop25.trendit.service.ExpoPushService;
import com.develop25.trendit.service.TopProductStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final PushTokenRepository pushTokenRepository;
    private final NotificationSettingRepository settingRepository;
    private final ExpoPushService pushService;
    private final TopProductStatService productStatService; // TOP5 상품 조회 서비스 (가정)

    private static final int BATCH_SIZE = 100; // 배치 처리 크기

    // 매 10분마다 실행 (예: 0분, 10분, 20분...)
    @Scheduled(cron = "0 */10 * * * *")
    public void scheduleDailyTop5Notification() throws InterruptedException {
        System.out.println("알림 스케줄러 실행: " + ZonedDateTime.now());

        // 1. TOP5 상품 집계 (모든 사용자에게 공통으로 발송할 메시지)
        List<TopProductStatDto> top5Products = productStatService.getLatestTop5();

        if (top5Products.isEmpty()) {
            System.out.println("TOP5 상품 집계 결과 없음. 스케줄러 종료.");
            return;
        }

        // 2. 현재 시점(10분 단위)에 알림을 받을 사용자 설정 조회 (SQL에서 타임존 고려 필요)
        List<NotificationSetting> targetSettings = findTargetNotificationSettings();

        // 3. 알림 발송 대상 사용자별 푸시 토큰과 설정을 결합하여 알림 메시지 생성
        List<PushNotificationDto> notifications = targetSettings.stream()
                .flatMap(setting ->
                        pushTokenRepository.findAllByUserIdAndIsActiveTrue(setting.getUserId()).stream() // 사용자별 활성 토큰 조회 [cite: 23]
                                .map(token -> createNotificationDto(token, top5Products))
                )
                .collect(Collectors.toList());

        if (notifications.isEmpty()) {
            System.out.println("알림 발송 대상 없음");
            return;
        }

        System.out.println(notifications.size() + "명에게 알림 발송 시작");

        // 4. 배치 처리 및 발송 [cite: 440]
        for (int i = 0; i < notifications.size(); i += BATCH_SIZE) {
            List<PushNotificationDto> batch = notifications.subList(i, Math.min(i + BATCH_SIZE, notifications.size()));

            // **재시도 로직은 ExpoPushService 내에서 구현하거나 별도로 AOP 적용
            pushService.sendPushNotifications(batch);

            // Rate limiting 방지를 위한 대기
            if (i + BATCH_SIZE < notifications.size()) {
                TimeUnit.SECONDS.sleep(1);
            }
        }

        System.out.println("알림 발송 완료");
    }

    /**
     * 현재 시각에 알림이 설정된 사용자 조회 (타임존 고려)
     */
    private List<NotificationSetting> findTargetNotificationSettings() {
        LocalTime currentTimeSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalTime(); // 서버 기본 타임존 가정
        int currentHour = currentTimeSeoul.getHour();
        // 10분 단위로 올림 (00, 10, 20, ...)
        int currentMinuteBucket = (currentTimeSeoul.getMinute() / 10) * 10;

        // **⚠️ 중요: DB 쿼리에서 사용자의 Timezone과 notification_time을 모두 고려하여 조회해야 합니다.**
        // 복잡한 Timezone 처리는 DB에서 `AT TIME ZONE` 구문을 사용하거나,
        // 모든 설정을 가져와 메모리에서 필터링하거나,
        // 10분 단위의 시간대를 기준으로 쿼리를 나누는 방법 등이 있습니다.

        // 임시 방편으로, 현재 시간(서울 타임존)과 일치하는 설정만 조회한다고 가정 (DB 쿼리 최적화 필요)
        return settingRepository.findAll().stream()
                .filter(NotificationSetting::isEnabled) // 활성화된 알림만 [cite: 467]
                .filter(setting -> setting.getNotificationTime().getHour() == currentHour)
                .filter(setting -> (setting.getNotificationTime().getMinute() / 10) * 10 == currentMinuteBucket)
                .collect(Collectors.toList());

        // 실제 구현 시 JPA/JPQL/QueryDSL로 최적화된 DB 쿼리 사용 권장
    }

    /**
     * PushNotificationDto 생성
     */
    private PushNotificationDto createNotificationDto(PushToken token, List<TopProductStatDto> top5Products) {
        // 알림 본문 포맷팅 [cite: 429]
        String body = formatTop5Message(top5Products);

        return new PushNotificationDto(
                token.getPushToken(),
                "현재 인기 상품 TOP5", //
                body,
                // Data Payload (앱에서 사용할 데이터)
                new DataPayloadDto("top5_products", top5Products),
                "default",
                1,
                "high",
                "default"
        );
    }

    /**
     * TOP5 상품 메시지를 포맷팅하여 알림 본문(Body)에 사용할 문자열을 생성합니다.
     * (Ranking과 상품 이름만 사용)
     */
    private String formatTop5Message(List<TopProductStatDto> products) {
        if (products == null || products.isEmpty()) {
            return "현재 인기 상품 정보가 없습니다.";
        }

        return products.stream()
                .map(product -> {
                    String salesFormatted = String.format("%,d", product.getSalesVolume());

                    // 최종 포맷: {순위} {상품 이름} ({판매량 또는 가격})
                    return String.format("%d %s (%s개)",
                            product.getRanking(),
                            product.getProductName(),
                            salesFormatted);
                })
                .collect(Collectors.joining("\n"));
    }
}
