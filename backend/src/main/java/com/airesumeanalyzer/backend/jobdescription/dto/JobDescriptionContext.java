package com.airesumeanalyzer.backend.jobdescription.dto;

import java.util.UUID;

public record JobDescriptionContext(
        UUID id,
        String displayName
) {
}
