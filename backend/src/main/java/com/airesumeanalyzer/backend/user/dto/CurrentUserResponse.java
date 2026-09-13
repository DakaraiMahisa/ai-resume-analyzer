package com.airesumeanalyzer.backend.user.dto;

import java.util.UUID;

public record CurrentUserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String role
) {
}

