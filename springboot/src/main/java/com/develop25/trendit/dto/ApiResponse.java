package com.develop25.trendit.dto;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {}