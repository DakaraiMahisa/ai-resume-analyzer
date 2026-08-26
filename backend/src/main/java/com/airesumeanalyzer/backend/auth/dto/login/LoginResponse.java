package com.airesumeanalyzer.backend.auth.dto.login;

public record LoginResponse(

        String accessToken,

        String refreshToken,

        String tokenType,

        long expiresIn

) {
}