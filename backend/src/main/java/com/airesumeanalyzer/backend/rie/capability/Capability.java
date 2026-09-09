package com.airesumeanalyzer.backend.rie.capability;

import java.util.Objects;

public record Capability(
        String name
) {

    public Capability {
        Objects.requireNonNull(name, "name must not be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }

        name = name.trim();
    }
}