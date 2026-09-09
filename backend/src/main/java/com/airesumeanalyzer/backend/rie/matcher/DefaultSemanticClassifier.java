package com.airesumeanalyzer.backend.rie.matcher;

import com.airesumeanalyzer.backend.ai.provider.AiModelProvider;
import com.airesumeanalyzer.backend.rie.classifier.dto.SemanticClassificationResponse;
import com.airesumeanalyzer.backend.rie.domain.MatchRelationship;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public final class DefaultSemanticClassifier
        implements SemanticClassifier {

    private final AiModelProvider aiModelProvider;

    public DefaultSemanticClassifier(
            AiModelProvider aiModelProvider
    ) {
        this.aiModelProvider =
                Objects.requireNonNull(
                        aiModelProvider,
                        "aiModelProvider must not be null"
                );
    }

    @Override
    public MatchRelationship classify(
            String requirementValue,
            String resumeValue,
            double similarity
    ) {
        if (requirementValue == null
                || requirementValue.isBlank()) {
            throw new IllegalArgumentException(
                    "requirementValue must not be blank"
            );
        }

        if (resumeValue == null
                || resumeValue.isBlank()) {
            throw new IllegalArgumentException(
                    "resumeValue must not be blank"
            );
        }

        if (!Double.isFinite(similarity)) {
            throw new IllegalArgumentException(
                    "similarity must be finite"
            );
        }

        String prompt = buildPrompt(
                requirementValue,
                resumeValue,
                similarity
        );

        SemanticClassificationResponse response =
                aiModelProvider.generate(
                        prompt,
                        SemanticClassificationResponse.class
                );

        if (response == null) {
            throw new IllegalStateException(
                    "AI semantic classifier returned null response"
            );
        }

        MatchRelationship relationship =
                Objects.requireNonNull(
                        response.relationship(),
                        "AI semantic classifier returned null relationship"
                );

        return switch (relationship) {
            case EQUIVALENT, SATISFIES, RELATED, UNRELATED -> relationship;
            case EXACT -> throw new IllegalStateException(
                    "Semantic classifier must not return EXACT"
            );
        };
    }

    private String buildPrompt(
            String requirementValue,
            String resumeValue,
            double similarity
    ) {
        return """
                Determine the semantic relationship between a job
                requirement and a resume capability.

                Requirement:
                %s

                Resume capability:
                %s

                Embedding similarity:
                %.4f

                Classify the relationship using exactly one of:

                EQUIVALENT:
                The resume capability represents essentially the same
                capability as the requirement, even if the wording differs.

                SATISFIES:
                The resume capability is sufficiently specific, broader,
                stronger, or otherwise demonstrates the capability required.

                RELATED:
                The capabilities are meaningfully related, but the resume
                capability does not establish that the requirement is satisfied.

                UNRELATED:
                The capabilities are not meaningfully related.

                Important rules:

                - Do not classify something as SATISFIES merely because it
                  is technically related to the requirement.
                - Do not assume that one technology automatically satisfies
                  another technology merely because they belong to the same
                  ecosystem.
                - Do not infer experience, proficiency, seniority, years of
                  experience, or knowledge that is not represented by the
                  resume capability.
                - Do not use the embedding similarity as the relationship
                  decision by itself.
                - EXACT is not a valid semantic classification. Exact matches
                  are handled by deterministic canonical matching.

                Return only the structured classification response.
                """.formatted(
                requirementValue.trim(),
                resumeValue.trim(),
                similarity
        );
    }
}