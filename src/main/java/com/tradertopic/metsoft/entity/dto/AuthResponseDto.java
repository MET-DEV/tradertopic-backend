package com.tradertopic.metsoft.entity.dto;

public record AuthResponseDto(String accessToken, String refreshToken, String tokenType) {
    public AuthResponseDto(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, "Bearer");
    }
}
