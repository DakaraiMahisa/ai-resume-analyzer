package com.airesumeanalyzer.backend.rie.requirement;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.rie.domain.RequirementToEvaluate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
public class RequirementClaimConsistencyValidator {

    public void validate(
            List<RequirementToEvaluate> requirements,
            List<Claim> requirementClaims
    ) {
        Objects.requireNonNull(
                requirements,
                "requirements must not be null"
        );
        Objects.requireNonNull(
                requirementClaims,
                "requirementClaims must not be null"
        );

        Set<String> persistedNames =
                requirementClaims.stream()
                        .filter(Objects::nonNull)
                        .map(Claim::getCanonicalName)
                        .filter(Objects::nonNull)
                        .map(this::normalize)
                        .collect(java.util.stream.Collectors.toSet());
        log.info(
                "Checking requirement claims. requirements={}, "
                        + "validatedClaimCount={}, distinctClaimNames={}",
                requirements.size(),
                requirementClaims.size(),
                persistedNames.size()
        );

        List<String> missingRequirements = new ArrayList<>();
        for (RequirementToEvaluate requirement : requirements) {
            if (requirement == null) {
                continue;
            }
            collectMissing(
                    requirement.requirement(),
                    requirement.priority().name(),
                    persistedNames,
                    missingRequirements,
                    new HashSet<>()
            );
        }

        if (!missingRequirements.isEmpty()) {
            log.error(
                    "Requirement claim consistency failed. missingCount={}, "
                            + "missingRequirements={}, validatedClaimNames={}",
                    missingRequirements.size(),
                    missingRequirements,
                    persistedNames
            );
            throw new RequirementClaimConsistencyException(
                    missingRequirements
            );
        }
        log.info("Requirement claim consistency passed.");
    }

    private void collectMissing(
            com.airesumeanalyzer.backend.ai.model.understanding
                    .StructuredJobDescription.Requirement requirement,
            String category,
            Set<String> persistedNames,
            List<String> missingRequirements,
            Set<String> visitedValues
    ) {
        if (requirement == null) {
            return;
        }

        if (requirement.value() != null
                && !requirement.value().isBlank()) {
            String value = requirement.value().trim();
            String normalized = normalize(value);
            if (persistedNames.contains(normalized)
                    || !visitedValues.add(category + "|" + normalized)) {
                // The second condition prevents duplicate diagnostics when
                // the same requirement occurs more than once in the tree.
            } else {
                missingRequirements.add(category + ": " + value);
            }
        }

        if (requirement.components() != null) {
            for (var component : requirement.components()) {
                collectMissing(
                        component,
                        category,
                        persistedNames,
                        missingRequirements,
                        visitedValues
                );
            }
        }
    }

    private String normalize(String value) {
        return value
                .trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }
}
