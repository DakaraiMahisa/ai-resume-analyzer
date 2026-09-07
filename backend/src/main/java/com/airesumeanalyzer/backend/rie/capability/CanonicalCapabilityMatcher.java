package com.airesumeanalyzer.backend.rie.capability;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.rie.domain.MatchRelationship;
import com.airesumeanalyzer.backend.rie.domain.MatchingMethod;
import com.airesumeanalyzer.backend.rie.domain.RequirementComponent;
import com.airesumeanalyzer.backend.rie.domain.RequirementMatch;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public final class CanonicalCapabilityMatcher
        implements CapabilityMatcher {

    private final CapabilityResolver capabilityResolver;

    public CanonicalCapabilityMatcher(
            CapabilityResolver capabilityResolver
    ) {
        this.capabilityResolver =
                Objects.requireNonNull(
                        capabilityResolver,
                        "capabilityResolver must not be null"
                );
    }

    @Override
    public Optional<RequirementMatch> match(
            RequirementComponent requirement,
            Claim resumeClaim
    ) {

        Objects.requireNonNull(
                requirement,
                "requirement must not be null"
        );

        Objects.requireNonNull(
                resumeClaim,
                "resumeClaim must not be null"
        );

        String resumeValue =
                resumeClaim.getCanonicalName();

        if (resumeValue == null || resumeValue.isBlank()) {
            return Optional.empty();
        }

        Capability requirementCapability =
                capabilityResolver.resolve(
                        requirement.value()
                );

        Capability resumeCapability =
                capabilityResolver.resolve(
                        resumeValue
                );

        if (!requirementCapability.name()
                .equalsIgnoreCase(
                        resumeCapability.name()
                )) {

            return Optional.empty();
        }

        return Optional.of(
                new RequirementMatch(
                        requirement.requirementClaimId(),
                        requirement.value(),
                        resumeClaim.getId(),
                        MatchRelationship.EXACT,
                        MatchingMethod.CANONICAL
                )
        );
    }
}