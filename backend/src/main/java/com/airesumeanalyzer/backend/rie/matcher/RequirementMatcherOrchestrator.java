package com.airesumeanalyzer.backend.rie.matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.rie.capability.CanonicalCapabilityMatcher;
import com.airesumeanalyzer.backend.rie.capability.CapabilityMatcher;
import com.airesumeanalyzer.backend.rie.capability.SemanticCapabilityMatcher;
import com.airesumeanalyzer.backend.rie.domain.RequirementComponent;
import com.airesumeanalyzer.backend.rie.domain.RequirementExpression;
import com.airesumeanalyzer.backend.rie.domain.RequirementGroup;
import com.airesumeanalyzer.backend.rie.domain.RequirementMatch;
import org.springframework.stereotype.Component;

@Component
public final class RequirementMatcherOrchestrator
        implements RequirementMatcher {

    private final CapabilityMatcher canonicalMatcher;
    private final CapabilityMatcher semanticMatcher;

    public RequirementMatcherOrchestrator(
            CanonicalCapabilityMatcher canonicalMatcher,
            SemanticCapabilityMatcher semanticMatcher
    ) {
        this.canonicalMatcher =
                Objects.requireNonNull(
                        canonicalMatcher,
                        "canonicalMatcher must not be null"
                );

        this.semanticMatcher =
                Objects.requireNonNull(
                        semanticMatcher,
                        "semanticMatcher must not be null"
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

            return matchComponent(
                    component,
                    resumeClaim
            );
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

    private List<RequirementMatch> matchComponent(
            RequirementComponent requirement,
            Claim resumeClaim
    ) {
        return canonicalMatcher
                .match(requirement, resumeClaim)
                .or(() ->
                        semanticMatcher.match(
                                requirement,
                                resumeClaim
                        )
                )
                .stream()
                .toList();
    }
}