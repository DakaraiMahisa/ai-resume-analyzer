package com.airesumeanalyzer.backend.auth.dto.logout;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(

        @NotBlank(message = "Refresh token is required")
        String refreshToken

) {
}