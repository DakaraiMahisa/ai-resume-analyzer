package com.airesumeanalyzer.backend.career.languages.dto.request;

import com.airesumeanalyzer.backend.career.languages.enums.LanguageProficiency;
import jakarta.validation.constraints.Size;

public record UpdateCandidateLanguageRequest(

        @Size(max = 100)
        String languageName,

        LanguageProficiency speakingLevel,

        LanguageProficiency listeningLevel,

        LanguageProficiency readingLevel,

        LanguageProficiency writingLevel,

        Boolean nativeLanguage
) {
}
