package com.airesumeanalyzer.backend.ai.parser.impl;

import com.airesumeanalyzer.backend.ai.model.claims.ClaimPriority;
import com.airesumeanalyzer.backend.ai.model.claims.ClaimType;
import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import com.airesumeanalyzer.backend.ai.model.understanding.StructuredJobDescription;
import com.airesumeanalyzer.backend.ai.model.understanding.StructuredResume;
import com.airesumeanalyzer.backend.ai.parser.AiResponseParser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AiResponseParserImpl implements AiResponseParser {


    @Override
    public List<ProposedClaim> parseResume(
            StructuredResume structuredResume,
            UUID sourceDocumentId
    ) {

        List<ProposedClaim> claims = new ArrayList<>();

        structuredResume.skills()
                .forEach(skill ->
                        claims.add(
                                ProposedClaim.builder()
                                        .claimType(ClaimType.SKILL)
                                        .extractedValue(skill)
                                        .priority(ClaimPriority.NOT_APPLICABLE)
                                        .sourceDocumentId(sourceDocumentId)
                                        .build()
                        )
                );

        structuredResume.experience()
                .forEach(experience -> {
                    List<String> evidenceTerms =
                            nonBlankValues(
                                    experience.jobTitle(),
                                    experience.company(),
                                    experience.startDate(),
                                    experience.endDate(),
                                    experience.description()
                            );

                    addResumeClaim(
                            claims,
                            ClaimType.EXPERIENCE,
                            joinNonBlank(
                                    " at ",
                                    experience.jobTitle(),
                                    experience.company()
                            ),
                            evidenceTerms,
                            sourceDocumentId
                    );
                    addResumeClaim(
                            claims,
                            ClaimType.EXPERIENCE,
                            experience.jobTitle(),
                            evidenceTerms,
                            sourceDocumentId
                    );
                    addResumeClaim(
                            claims,
                            ClaimType.EXPERIENCE,
                            experience.company(),
                            evidenceTerms,
                            sourceDocumentId
                    );
                    addResumeClaim(
                            claims,
                            ClaimType.EXPERIENCE,
                            experience.description(),
                            evidenceTerms,
                            sourceDocumentId
                    );
                });

        structuredResume.education()
                .forEach(education -> {

                    List<String> evidenceTerms =
                            nonBlankValues(
                                    education.degree(),
                                    education.institution(),
                                    education.fieldOfStudy(),
                                    education.startDate(),
                                    education.endDate()
                            );

                    addResumeClaim(
                            claims,
                            ClaimType.EDUCATION,
                            joinNonBlank(
                                    " - ",
                                    education.degree(),
                                    education.fieldOfStudy(),
                                    education.institution()
                            ),
                            evidenceTerms,
                            sourceDocumentId
                    );
                    addResumeClaim(
                            claims,
                            ClaimType.EDUCATION,
                            education.degree(),
                            evidenceTerms,
                            sourceDocumentId
                    );
                    addResumeClaim(
                            claims,
                            ClaimType.EDUCATION,
                            education.fieldOfStudy(),
                            evidenceTerms,
                            sourceDocumentId
                    );
                    addResumeClaim(
                            claims,
                            ClaimType.EDUCATION,
                            education.institution(),
                            evidenceTerms,
                            sourceDocumentId
                    );
                });

        structuredResume.projects()
                .forEach(project -> {

                    claims.add(
                            ProposedClaim.builder()
                                    .claimType(ClaimType.PROJECT)
                                    .extractedValue(project.name())
                                    .priority(ClaimPriority.NOT_APPLICABLE)
                                    .sourceDocumentId(sourceDocumentId)
                                    .build()
                    );

                    if (project.technologies() != null) {
                        project.technologies()
                                .forEach(technology ->
                                        claims.add(
                                                ProposedClaim.builder()
                                                        .claimType(ClaimType.SKILL)
                                                        .extractedValue(technology)
                                                        .priority(ClaimPriority.NOT_APPLICABLE)
                                                        .sourceDocumentId(sourceDocumentId)
                                                        .build()
                                        )
                                );
                    }
                });

        structuredResume.certifications()
                .forEach(certification ->
                        claims.add(
                                ProposedClaim.builder()
                                        .claimType(ClaimType.CERTIFICATION)
                                        .extractedValue(certification)
                                        .priority(ClaimPriority.NOT_APPLICABLE)
                                        .sourceDocumentId(sourceDocumentId)
                                        .build()
                        )
                );

        return claims;
    }

    private void addResumeClaim(
            List<ProposedClaim> claims,
            ClaimType claimType,
            String value,
            List<String> evidenceSearchTerms,
            UUID sourceDocumentId
    ) {
        if (value == null || value.isBlank()) {
            return;
        }

        claims.add(
                ProposedClaim.builder()
                        .claimType(claimType)
                        .extractedValue(value.trim())
                        .evidenceSearchTerms(evidenceSearchTerms)
                        .priority(ClaimPriority.NOT_APPLICABLE)
                        .sourceDocumentId(sourceDocumentId)
                        .build()
        );
    }

    private List<String> nonBlankValues(String... values) {
        return Stream.of(values)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
    }

    private String joinNonBlank(String separator, String... values) {
        return nonBlankValues(values)
                .stream()
                .collect(Collectors.joining(separator));
    }


    @Override
    public List<ProposedClaim> parseJobDescription(
            StructuredJobDescription structuredJobDescription,
            UUID sourceDocumentId
    ) {

        List<ProposedClaim> claims = new ArrayList<>();

        structuredJobDescription.requiredSkills()
                .forEach(requirement ->
                        addRequirementClaims(
                                requirement,
                                ClaimType.SKILL,
                                ClaimPriority.REQUIRED,
                                sourceDocumentId,
                                claims
                        )
                );

        structuredJobDescription.preferredSkills()
                .forEach(requirement ->
                        addRequirementClaims(
                                requirement,
                                ClaimType.SKILL,
                                ClaimPriority.PREFERRED,
                                sourceDocumentId,
                                claims
                        )
                );

        structuredJobDescription.responsibilities()
                .forEach(responsibility ->
                        claims.add(
                                ProposedClaim.builder()
                                        .claimType(ClaimType.RESPONSIBILITY)
                                        .extractedValue(responsibility)
                                        .priority(ClaimPriority.NOT_APPLICABLE)
                                        .sourceDocumentId(sourceDocumentId)
                                        .build()
                        )
                );

        structuredJobDescription.requiredExperience()
                .forEach(requirement ->
                        addRequirementClaims(
                                requirement,
                                ClaimType.EXPERIENCE,
                                ClaimPriority.REQUIRED,
                                sourceDocumentId,
                                claims
                        )
                );

        structuredJobDescription.preferredExperience()
                .forEach(requirement ->
                        addRequirementClaims(
                                requirement,
                                ClaimType.EXPERIENCE,
                                ClaimPriority.PREFERRED,
                                sourceDocumentId,
                                claims
                        )
                );

        structuredJobDescription.requiredQualifications()
                .forEach(requirement ->
                        addRequirementClaims(
                                requirement,
                                ClaimType.EDUCATION,
                                ClaimPriority.REQUIRED,
                                sourceDocumentId,
                                claims
                        )
                );

        structuredJobDescription.preferredQualifications()
                .forEach(requirement ->
                        addRequirementClaims(
                                requirement,
                                ClaimType.EDUCATION,
                                ClaimPriority.PREFERRED,
                                sourceDocumentId,
                                claims
                        )
                );

        return claims;
    }

    private void addRequirementClaims(
            StructuredJobDescription.Requirement requirement,
            ClaimType claimType,
            ClaimPriority priority,
            UUID sourceDocumentId,
            List<ProposedClaim> claims
    ) {

        if (requirement == null) {
            return;
        }

        if (requirement.value() != null
                && !requirement.value().isBlank()) {

            claims.add(
                    ProposedClaim.builder()
                            .claimType(claimType)
                            .extractedValue(requirement.value())
                            .originalText(requirement.originalText())
                            .priority(priority)
                            .sourceDocumentId(sourceDocumentId)
                            .build()
            );
        }

        if (requirement.components() == null) {
            return;
        }

        requirement.components()
                .forEach(component ->
                        addRequirementClaims(
                                component,
                                claimType,
                                priority,
                                sourceDocumentId,
                                claims
                        )
                );
    }

}