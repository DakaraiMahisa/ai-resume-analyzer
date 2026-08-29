package com.airesumeanalyzer.backend.ai.model.validation;

import lombok.Builder;

@Builder
public record ValidationResult(

        boolean valid,

        String validator,

        String message

) {
}