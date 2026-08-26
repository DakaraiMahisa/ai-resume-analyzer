package com.airesumeanalyzer.backend.auth.dto.refresh;

public record RefreshTokenResponse(

        String accessToken,

        String refreshToken,

        String tokenType,

        long expiresIn

) {
}
