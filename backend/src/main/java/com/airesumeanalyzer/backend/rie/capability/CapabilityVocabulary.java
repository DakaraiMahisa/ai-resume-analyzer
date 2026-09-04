package com.airesumeanalyzer.backend.rie.capability;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
public final class CapabilityVocabulary {

    private final Map<String, String> terms = Map.of(
            "ml", "machine learning",
            "machine learning", "machine learning",
            "js", "javascript",
            "javascript", "javascript",
            "postgres", "postgresql",
            "postgresql", "postgresql",
            "aws", "amazon web services",
            "amazon web services", "amazon web services"
    );

    public String resolve(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return terms.get(
                value.trim().toLowerCase(Locale.ROOT)
        );
    }
}