package com.airesumeanalyzer.backend.career.languages.dto.response;

import com.airesumeanalyzer.backend.career.languages.enums.LanguageProficiency;

import java.time.Instant;
import java.util.UUID;

public record CandidateLanguageResponse(

        UUID id,

        String languageName,

        LanguageProficiency speakingLevel,

        LanguageProficiency listeningLevel,

        LanguageProficiency readingLevel,

        LanguageProficiency writingLevel,

        boolean nativeLanguage,

        Instant createdAt,

        Instant updatedAt
) {
}
