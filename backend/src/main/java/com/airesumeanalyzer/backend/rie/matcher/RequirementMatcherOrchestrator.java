package com.airesumeanalyzer.backend.rie.matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Map;
import java.util.UUID;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.ai.model.claims.ClaimType;
import com.airesumeanalyzer.backend.rie.capability.CanonicalCapabilityMatcher;
import com.airesumeanalyzer.backend.rie.capability.CapabilityMatcher;
import com.airesumeanalyzer.backend.rie.capability.SemanticCapabilityMatcher;
import com.airesumeanalyzer.backend.rie.capability.SemanticMatchCandidate;
import com.airesumeanalyzer.backend.rie.domain.RequirementComponent;
import com.airesumeanalyzer.backend.rie.domain.RequirementExpression;
import com.airesumeanalyzer.backend.rie.domain.RequirementMatch;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public final class RequirementMatcherOrchestrator
        implements RequirementMatcher {

    private final CapabilityMatcher canonicalMatcher;
    private final SemanticCapabilityMatcher semanticMatcher;

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
    public RequirementMatchingResult match(
            RequirementExpression requirement,
            Map<UUID, ClaimType> requirementClaimTypes,
            List<Claim> resumeClaims,
            EmbeddingContext embeddingContext
    ) {
        Objects.requireNonNull(
                requirement,
                "requirement must not be null"
        );

        Objects.requireNonNull(
                requirementClaimTypes,
                "requirementClaimTypes must not be null"
        );
        Objects.requireNonNull(
                resumeClaims,
                "resumeClaims must not be null"
        );
        Objects.requireNonNull(
                embeddingContext,
                "embeddingContext must not be null"
        );
        if (resumeClaims.isEmpty()) {
            return new RequirementMatchingResult(
                    List.of(),
                    List.of()
            );
        }

        List<RequirementComponent> components =
                RequirementExpressionComponents.extract(
                        requirement
                );

        List<RequirementMatch> matches =
                new ArrayList<>();

        List<SemanticMatchCandidate> semanticCandidates =
                new ArrayList<>();
        for (RequirementComponent component : components) {
            ClaimType requirementType =
                    requirementClaimTypes.get(component.requirementClaimId());

            for (Claim resumeClaim : resumeClaims) {

                if (resumeClaim == null) {
                    continue;
                }
                if (!isCompatible(requirementType, resumeClaim.getClaimType())) {
                    log.debug(
                            "Resume claim skipped due to type incompatibility. "
                                    + "requirementValue={}, requirementType={}, "
                                    + "resumeClaimId={}, resumeClaimType={}",
                            component.value(),
                            requirementType,
                            resumeClaim.getId(),
                            resumeClaim.getClaimType()
                    );
                    continue;
                }

                Optional<RequirementMatch> canonicalMatch =
                        canonicalMatcher.match(
                                component,
                                resumeClaim
                        );

                if (canonicalMatch.isPresent()) {

                    matches.add(
                            canonicalMatch.get()
                    );
                    log.debug(
                            "Canonical requirement match. requirementValue={}, "
                                    + "resumeClaimId={}, resumeClaimValue={}, "
                                    + "relationship={}",
                            component.value(),
                            resumeClaim.getId(),
                            resumeClaim.getCanonicalName(),
                            canonicalMatch.get().relationship()
                    );

                    continue;
                }

                Optional<SemanticMatchCandidate> semanticCandidate =
                        semanticMatcher.match(
                                component,
                                resumeClaim,
                                embeddingContext
                        );

                semanticCandidate.ifPresent(
                        candidate -> {
                            semanticCandidates.add(candidate);
                            log.debug(
                                    "Semantic match candidate created. "
                                            + "requirementValue={}, "
                                            + "resumeClaimId={}, "
                                            + "resumeClaimValue={}, candidate={}",
                                    component.value(),
                                    resumeClaim.getId(),
                                    resumeClaim.getCanonicalName(),
                                    candidate
                            );
                        }
                );
            }
        }

        log.debug(
                "Requirement matching pass completed. componentCount={}, "
                        + "canonicalMatchCount={}, semanticCandidateCount={}",
                components.size(),
                matches.size(),
                semanticCandidates.size()
        );

        return new RequirementMatchingResult(
                matches,
                semanticCandidates
        );
    }

    private boolean isCompatible(
            ClaimType requirementType,
            ClaimType resumeType
    ) {
        if (requirementType == null || resumeType == null) {
            return true;
        }

        return switch (requirementType) {
            case EDUCATION -> resumeType == ClaimType.EDUCATION
                    || resumeType == ClaimType.CERTIFICATION;
            case EXPERIENCE -> resumeType == ClaimType.EXPERIENCE
                    || resumeType == ClaimType.PROJECT
                    || resumeType == ClaimType.SKILL
                    || resumeType == ClaimType.TECHNICAL_TOOL;
            default -> true;
        };
    }

}