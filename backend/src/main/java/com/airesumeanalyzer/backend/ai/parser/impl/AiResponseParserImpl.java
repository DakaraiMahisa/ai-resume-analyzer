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
                .forEach(experience ->
                        claims.add(
                                ProposedClaim.builder()
                                        .claimType(ClaimType.EXPERIENCE)
                                        .extractedValue(
                                                experience.jobTitle()
                                                        + " at "
                                                        + experience.company()
                                        )
                                        .priority(ClaimPriority.NOT_APPLICABLE)
                                        .sourceDocumentId(sourceDocumentId)
                                        .build()
                        )
                );

        structuredResume.education()
                .forEach(education -> {

                    String educationValue =
                            Stream.of(
                                            education.degree(),
                                            education.fieldOfStudy(),
                                            education.institution()
                                    )
                                    .filter(Objects::nonNull)
                                    .filter(value -> !value.isBlank())
                                    .collect(Collectors.joining(" - "));

                    claims.add(
                            ProposedClaim.builder()
                                    .claimType(ClaimType.EDUCATION)
                                    .extractedValue(educationValue)
                                    .priority(ClaimPriority.NOT_APPLICABLE)
                                    .sourceDocumentId(sourceDocumentId)
                                    .build()
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