package com.airesumeanalyzer.backend.rie.capability;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public final class DefaultCapabilityResolver
        implements CapabilityResolver {

    private final CapabilityVocabulary vocabulary;

    public DefaultCapabilityResolver(
            CapabilityVocabulary vocabulary
    ) {
        this.vocabulary = Objects.requireNonNull(
                vocabulary,
                "vocabulary must not be null"
        );
    }

    @Override
    public Capability resolve(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "value must not be blank"
            );
        }

        String normalizedValue = value.trim();

        String resolvedName = vocabulary.resolve(normalizedValue);

        if (resolvedName == null || resolvedName.isBlank()) {
            resolvedName = normalizedValue;
        }

        return new Capability(resolvedName);
    }
}