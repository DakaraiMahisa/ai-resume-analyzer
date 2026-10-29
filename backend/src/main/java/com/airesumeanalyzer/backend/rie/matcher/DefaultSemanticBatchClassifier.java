package com.airesumeanalyzer.backend.rie.matcher;

import com.airesumeanalyzer.backend.ai.provider.AiModelProvider;
import com.airesumeanalyzer.backend.rie.capability.SemanticMatchCandidate;
import com.airesumeanalyzer.backend.rie.classifier.dto.SemanticClassificationBatchResponse;
import com.airesumeanalyzer.backend.rie.classifier.dto.SemanticClassificationItem;
import com.airesumeanalyzer.backend.rie.domain.MatchRelationship;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public final class DefaultSemanticBatchClassifier
        implements SemanticBatchClassifier {

    private final AiModelProvider aiModelProvider;

    public DefaultSemanticBatchClassifier(
            @Qualifier("geminiModelProvider")
            AiModelProvider aiModelProvider
    ) {
        this.aiModelProvider =
                Objects.requireNonNull(
                        aiModelProvider,
                        "aiModelProvider must not be null"
                );
    }

    @Override
    public Map<SemanticMatchCandidate, MatchRelationship> classifyBatch(
            List<SemanticMatchCandidate> candidates
    ) {
        Objects.requireNonNull(
                candidates,
                "candidates must not be null"
        );

        if (candidates.isEmpty()) {
            return Map.of();
        }

        String prompt = buildPrompt(candidates);

        SemanticClassificationBatchResponse response =
                aiModelProvider.generate(
                        prompt,
                        SemanticClassificationBatchResponse.class
                );

        if (response == null
                || response.classifications() == null) {
            throw new IllegalStateException(
                    "AI semantic batch classifier returned null response"
            );
        }

        Map<SemanticMatchCandidate, MatchRelationship> classifications =
                new LinkedHashMap<>();

        for (SemanticClassificationItem item :
                response.classifications()) {

            if (item == null) {
                throw new IllegalStateException(
                        "AI semantic batch classifier returned null item"
                );
            }

            int index = item.candidateIndex();

            if (index < 0 || index >= candidates.size()) {
                throw new IllegalStateException(
                        "AI semantic batch classifier returned invalid candidate index: "
                                + index
                );
            }

            SemanticMatchCandidate candidate =
                    candidates.get(index);

            MatchRelationship relationship =
                    Objects.requireNonNull(
                            item.relationship(),
                            "AI semantic batch classifier returned null relationship"
                    );

            if (relationship == MatchRelationship.EXACT) {
                throw new IllegalStateException(
                        "Semantic batch classifier must not return EXACT"
                );
            }

            if (classifications.put(
                    candidate,
                    relationship
            ) != null) {
                throw new IllegalStateException(
                        "AI semantic batch classifier returned duplicate candidate index: "
                                + index
                );
            }
        }

        if (classifications.size() != candidates.size()) {
            throw new IllegalStateException(
                    "AI semantic batch classifier did not classify every candidate"
            );
        }

        return Map.copyOf(classifications);
    }

    private String buildPrompt(
            List<SemanticMatchCandidate> candidates
    ) {
        StringBuilder prompt =
                new StringBuilder("""
                    You are a semantic matching classifier used by a
                    resume-to-job-requirement analysis system.

                    Your task is to classify EVERY candidate below by
                    determining the semantic relationship between the
                    job requirement and the resume capability.

                    You must independently evaluate each candidate.
                    Do not assume that a high embedding similarity means
                    the requirement is satisfied.

                    ==================================================
                    ALLOWED RELATIONSHIPS
                    ==================================================

                    EQUIVALENT:
                    The resume capability expresses essentially the same
                    capability as the requirement, despite differences
                    in wording, naming, abbreviation, or phrasing.

                    Examples:
                    - Requirement: "Java"
                      Resume: "Java programming"

                    - Requirement: "PostgreSQL"
                      Resume: "Postgres"

                    - Requirement: "REST APIs"
                      Resume: "RESTful API development"

                    SATISFIES:
                    The resume capability is sufficient to demonstrate
                    the capability required by the job requirement.

                    This may occur when the resume capability is:
                    - a more specific form of the required capability,
                    - a broader capability that genuinely encompasses
                      the required capability,
                    - a stronger/superset capability that reasonably
                      includes the requirement.

                    However, the relationship must be defensible from
                    the capability names themselves. Do not invent
                    experience or knowledge.

                    Examples:
                    - Requirement: "Spring"
                      Resume: "Spring Boot"

                    - Requirement: "SQL"
                      Resume: "PostgreSQL"

                    - Requirement: "Object-oriented programming"
                      Resume: "Java programming"

                    RELATED:
                    The two capabilities belong to the same or a closely
                    related technical/domain area, but the resume
                    capability does NOT establish that the requirement
                    itself is satisfied.

                    Examples:
                    - Requirement: "React"
                      Resume: "Angular"

                    - Requirement: "PostgreSQL"
                      Resume: "MySQL"

                    - Requirement: "Docker"
                      Resume: "Kubernetes"

                    - Requirement: "Java"
                      Resume: "JavaScript"

                    UNRELATED:
                    The capabilities do not have a meaningful semantic
                    relationship relevant to the requirement.

                    ==================================================
                    DECISION RULES
                    ==================================================

                    1. Evaluate semantic meaning, not merely lexical
                       similarity.

                    2. Embedding similarity is ONLY a candidate-selection
                       signal. It is NOT the final classification criterion.

                    3. Do NOT classify a capability as SATISFIES merely
                       because it is:
                       - in the same ecosystem,
                       - used with the requirement,
                       - commonly used alongside the requirement,
                       - conceptually adjacent,
                       - a popular alternative,
                       - a prerequisite,
                       - a dependent technology.

                    4. Do NOT assume that similar technologies are
                       interchangeable.

                    5. Do NOT assume that one technology automatically
                       demonstrates knowledge of another technology.

                    6. A related technology should normally be classified
                       as RELATED unless the resume capability itself
                       clearly encompasses or satisfies the requirement.

                    7. Do NOT infer:
                       - years of experience,
                       - proficiency,
                       - seniority,
                       - project experience,
                       - production experience,
                       - certifications,
                       - knowledge not explicitly represented by the
                         capability value.

                    8. Do NOT use external knowledge to invent evidence
                       about the candidate's experience.

                    9. Prefer EQUIVALENT when the two values are simply
                       different names, abbreviations, or representations
                       of essentially the same capability.

                    10. Prefer SATISFIES only when there is a defensible
                        capability hierarchy or genuine semantic
                        containment.

                    11. Use RELATED when the relationship is real but
                        insufficient to establish that the requirement
                        is satisfied.

                    12. Use UNRELATED when there is no meaningful
                        capability relationship.

                    13. EXACT is NOT a valid semantic classification.
                        Exact matches are handled separately by the
                        deterministic canonical matcher.

                    14. Never create a classification that is not one
                        of:
                        EQUIVALENT, SATISFIES, RELATED, UNRELATED.

                    ==================================================
                    BATCH PROCESSING RULES
                    ==================================================

                    - Classify every candidate exactly once.
                    - Preserve candidateIndex exactly as provided.
                    - Do not skip candidates.
                    - Do not merge candidates.
                    - Do not create new candidates.
                    - Do not change candidateIndex values.
                    - The number of classifications MUST equal the
                      number of candidates supplied.
                    - Each candidateIndex must appear exactly once.
                    - The classification order does not matter, but every
                      candidateIndex must be present.
                    - Return no explanation, commentary, reasoning, or
                      additional fields beyond the structured response.

                    ==================================================
                    CANDIDATES
                    ==================================================

                    """);

        for (int index = 0;
             index < candidates.size();
             index++) {

            SemanticMatchCandidate candidate =
                    candidates.get(index);

            prompt
                    .append("Candidate ")
                    .append(index)
                    .append(":\n")
                    .append("candidateIndex: ")
                    .append(index)
                    .append("\n")
                    .append("Requirement: ")
                    .append(candidate.requirementValue())
                    .append("\n")
                    .append("Resume capability: ")
                    .append(candidate.resumeValue())
                    .append("\n")
                    .append("Embedding similarity: ")
                    .append("%.4f".formatted(
                            candidate.similarity()
                    ))
                    .append("\n\n");
        }

        prompt.append("""
                ==================================================
                OUTPUT
                ==================================================

                Return ONLY the structured batch classification response.

                For every candidate, return:
                - candidateIndex
                - relationship

                relationship MUST be exactly one of:
                EQUIVALENT
                SATISFIES
                RELATED
                UNRELATED

                Do not return markdown.
                Do not return prose.
                Do not return explanations.
                Do not omit any candidate.
                """);

        return prompt.toString();
    }


}
