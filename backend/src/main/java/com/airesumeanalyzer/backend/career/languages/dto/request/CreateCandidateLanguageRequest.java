package com.airesumeanalyzer.backend.career.languages.dto.request;

import com.airesumeanalyzer.backend.career.languages.enums.LanguageProficiency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCandidateLanguageRequest(

        @NotBlank(message = "Language name is required")
        @Size(max = 100)
        String languageName,

        @NotNull(message = "Speaking proficiency is required")
        LanguageProficiency speakingLevel,

        @NotNull(message = "Listening proficiency is required")
        LanguageProficiency listeningLevel,

        @NotNull(message = "Reading proficiency is required")
        LanguageProficiency readingLevel,

        @NotNull(message = "Writing proficiency is required")
        LanguageProficiency writingLevel,

        boolean nativeLanguage
) {
}
