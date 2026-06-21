package com.tradertopic.metsoft.entity.dto;

public record AuthResponseDto(String token, String tokenType) {
    public AuthResponseDto(String token) {
        this(token, "Bearer");
    }
}
