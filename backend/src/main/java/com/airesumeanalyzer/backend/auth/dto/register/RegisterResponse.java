package com.airesumeanalyzer.backend.auth.dto.register;

import java.util.UUID;

public record RegisterResponse(

        UUID userId,

        String email,

        String firstName,

        String lastName

) {
}