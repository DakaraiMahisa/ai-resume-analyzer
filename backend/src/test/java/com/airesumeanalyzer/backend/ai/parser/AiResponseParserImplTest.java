package com.airesumeanalyzer.backend.ai.parser;

import com.airesumeanalyzer.backend.ai.model.claims.ClaimPriority;
import com.airesumeanalyzer.backend.ai.model.claims.ClaimType;
import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import com.airesumeanalyzer.backend.ai.model.understanding.StructuredJobDescription;
import com.airesumeanalyzer.backend.ai.model.understanding.StructuredResume;
import com.airesumeanalyzer.backend.ai.parser.impl.AiResponseParserImpl;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiResponseParserImplTest {

    private final AiResponseParser parser =
            new AiResponseParserImpl();

    @Test
    void shouldParseResumeSkillsIntoProposedClaims() {

        UUID documentId = UUID.randomUUID();

        StructuredResume resume =
                StructuredResume.builder()
                        .skills(List.of(
                                "Java",
                                "Spring Boot",
                                "MySQL"
                        ))
                        .experience(List.of())
                        .education(List.of())
                        .projects(List.of())
                        .certifications(List.of())
                        .build();

        List<ProposedClaim> claims =
                parser.parseResume(resume, documentId);

        assertEquals(3, claims.size());

        assertTrue(
                claims.stream()
                        .allMatch(claim ->
                                claim.claimType() == ClaimType.SKILL
                        )
        );

        assertTrue(
                claims.stream()
                        .allMatch(claim ->
                                claim.priority() ==
                                        ClaimPriority.NOT_APPLICABLE
                        )
        );

        assertTrue(
                claims.stream()
                        .allMatch(claim ->
                                claim.sourceDocumentId()
                                        .equals(documentId)
                        )
        );
    }

    @Test
    void shouldPreserveRequiredAndPreferredPriorityForJobDescription() {

        UUID documentId = UUID.randomUUID();

        StructuredJobDescription jobDescription =
                StructuredJobDescription.builder()
                        .jobTitle("AI/ML Intern")
                        .summary("AI/ML role")
                        .requiredSkills(List.of(
                                "Python",
                                "SQL"
                        ))
                        .preferredSkills(List.of(
                                "Docker",
                                "RAG"
                        ))
                        .responsibilities(List.of(
                                "Build ML prototypes"
                        ))
                        .requiredExperience(List.of())
                        .preferredExperience(List.of(
                                "AI/ML internship"
                        ))
                        .requiredQualifications(List.of())
                        .preferredQualifications(List.of())
                        .build();

        List<ProposedClaim> claims =
                parser.parseJobDescription(
                        jobDescription,
                        documentId
                );

        assertEquals(6, claims.size());

        assertEquals(
                2,
                claims.stream()
                        .filter(claim ->
                                claim.priority() ==
                                        ClaimPriority.REQUIRED)
                        .count()
        );

        assertEquals(
                3,
                claims.stream()
                        .filter(claim ->
                                claim.priority() ==
                                        ClaimPriority.PREFERRED)
                        .count()
        );

        assertEquals(
                1,
                claims.stream()
                        .filter(claim ->
                                claim.claimType() ==
                                        ClaimType.RESPONSIBILITY)
                        .count()
        );

        assertTrue(
                claims.stream()
                        .allMatch(claim ->
                                claim.sourceDocumentId()
                                        .equals(documentId))
        );
    }
}