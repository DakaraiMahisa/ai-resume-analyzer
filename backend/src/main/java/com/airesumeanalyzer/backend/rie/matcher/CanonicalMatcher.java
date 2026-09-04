package com.airesumeanalyzer.backend.rie.matcher;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.rie.capability.Capability;
import com.airesumeanalyzer.backend.rie.capability.CapabilityResolver;
import com.airesumeanalyzer.backend.rie.domain.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public final class CanonicalMatcher
        implements RequirementMatcher {

    private final CapabilityResolver capabilityResolver;

    public CanonicalMatcher(
            CapabilityResolver capabilityResolver
    ) {
        this.capabilityResolver =
                Objects.requireNonNull(
                        capabilityResolver,
                        "capabilityResolver must not be null"
                );
    }

    @Override
    public List<RequirementMatch> match(
            RequirementExpression requirement,
            List<Claim> resumeClaims
    ) {
        Objects.requireNonNull(
                requirement,
                "requirement must not be null"
        );

        Objects.requireNonNull(
                resumeClaims,
                "resumeClaims must not be null"
        );

        if (resumeClaims.isEmpty()) {
            return List.of();
        }

        List<RequirementMatch> matches =
                new ArrayList<>();

        for (Claim resumeClaim : resumeClaims) {

            if (resumeClaim == null) {
                continue;
            }

            matches.addAll(
                    matchExpression(
                            requirement,
                            resumeClaim
                    )
            );
        }

        return List.copyOf(matches);
    }

    private List<RequirementMatch> matchExpression(
            RequirementExpression expression,
            Claim resumeClaim
    ) {

        if (expression instanceof RequirementComponent component) {

            RequirementMatch match =
                    matchComponent(
                            component,
                            resumeClaim
                    );

            return match == null
                    ? List.of()
                    : List.of(match);
        }

        if (expression instanceof RequirementGroup group) {

            List<RequirementMatch> matches =
                    new ArrayList<>();

            for (RequirementExpression child :
                    group.expressions()) {

                matches.addAll(
                        matchExpression(
                                child,
                                resumeClaim
                        )
                );
            }

            return List.copyOf(matches);
        }

        throw new IllegalStateException(
                "Unsupported requirement expression: "
                        + expression.getClass().getName()
        );
    }

    private RequirementMatch matchComponent(
            RequirementComponent requirementComponent,
            Claim resumeClaim
    ) {

        String resumeValue =
                resumeClaim.getCanonicalName();

        if (resumeValue == null
                || resumeValue.isBlank()) {
            return null;
        }

        Capability requirementCapability =
                capabilityResolver.resolve(
                        requirementComponent.value()
                );

        Capability resumeCapability =
                capabilityResolver.resolve(
                        resumeValue
                );

        if (!requirementCapability.name()
                .equalsIgnoreCase(
                        resumeCapability.name()
                )) {
            return null;
        }

        return new RequirementMatch(
                requirementComponent.requirementClaimId(),
                requirementComponent.value(),
                resumeClaim.getId(),
                MatchRelationship.EXACT_MATCH,
                MatchingMethod.CANONICAL
        );
    }
}

