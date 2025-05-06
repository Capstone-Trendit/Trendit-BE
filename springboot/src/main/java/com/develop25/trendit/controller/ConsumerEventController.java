package com.develop25.trendit.controller;

import com.develop25.trendit.dto.ConsumerEventDto;
import com.develop25.trendit.service.ConsumerEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class ConsumerEventController {

    private final ConsumerEventProducer eventProducer;

    @PostMapping
    public ResponseEntity<String> receiveEvent(@RequestBody ConsumerEventDto dto) {
        eventProducer.sendEvent(dto);
        return ResponseEntity.ok("소비자 이벤트 수신 완료");
    }
}