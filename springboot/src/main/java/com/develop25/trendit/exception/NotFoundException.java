package com.develop25.trendit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * HTTP 404 Not Found 응답 코드를 반환하는 사용자 정의 예외 클래스입니다.
 * 리소스를 찾을 수 없을 때 (예: 알림 설정을 찾을 수 없을 때) 사용됩니다.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // 이 예외가 발생하면 HTTP 상태 코드를 404로 설정
public class NotFoundException extends RuntimeException {

    // 기본 생성자
    public NotFoundException() {
        super("Requested resource not found.");
    }

    // 메시지를 인수로 받는 생성자 (사용자 정의 메시지 설정 가능)
    public NotFoundException(String message) {
        super(message);
    }

    // 메시지와 원인 예외를 인수로 받는 생성자
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}