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
import java.util.UUID;

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
                .forEach(education ->
                        claims.add(
                                ProposedClaim.builder()
                                        .claimType(ClaimType.EDUCATION)
                                        .extractedValue(
                                                education.degree()
                                                        + " - "
                                                        + education.institution()
                                        )
                                        .priority(ClaimPriority.NOT_APPLICABLE)
                                        .sourceDocumentId(sourceDocumentId)
                                        .build()
                        )
                );

        structuredResume.projects()
                .forEach(project ->
                        claims.add(
                                ProposedClaim.builder()
                                        .claimType(ClaimType.PROJECT)
                                        .extractedValue(project.name())
                                        .priority(ClaimPriority.NOT_APPLICABLE)
                                        .sourceDocumentId(sourceDocumentId)
                                        .build()
                        )
                );

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
                .forEach(skill ->
                        claims.add(
                                ProposedClaim.builder()
                                        .claimType(ClaimType.SKILL)
                                        .extractedValue(skill)
                                        .priority(ClaimPriority.REQUIRED)
                                        .sourceDocumentId(sourceDocumentId)
                                        .build()
                        )
                );

        structuredJobDescription.preferredSkills()
                .forEach(skill ->
                        claims.add(
                                ProposedClaim.builder()
                                        .claimType(ClaimType.SKILL)
                                        .extractedValue(skill)
                                        .priority(ClaimPriority.PREFERRED)
                                        .sourceDocumentId(sourceDocumentId)
                                        .build()
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
                .forEach(experience ->
                        claims.add(
                                ProposedClaim.builder()
                                        .claimType(ClaimType.EXPERIENCE)
                                        .extractedValue(experience)
                                        .priority(ClaimPriority.REQUIRED)
                                        .sourceDocumentId(sourceDocumentId)
                                        .build()
                        )
                );

        structuredJobDescription.preferredExperience()
                .forEach(experience ->
                        claims.add(
                                ProposedClaim.builder()
                                        .claimType(ClaimType.EXPERIENCE)
                                        .extractedValue(experience)
                                        .priority(ClaimPriority.PREFERRED)
                                        .sourceDocumentId(sourceDocumentId)
                                        .build()
                        )
                );

        structuredJobDescription.requiredQualifications()
                .forEach(qualification ->
                        claims.add(
                                ProposedClaim.builder()
                                        .claimType(ClaimType.EDUCATION)
                                        .extractedValue(qualification)
                                        .priority(ClaimPriority.REQUIRED)
                                        .sourceDocumentId(sourceDocumentId)
                                        .build()
                        )
                );

        structuredJobDescription.preferredQualifications()
                .forEach(qualification ->
                        claims.add(
                                ProposedClaim.builder()
                                        .claimType(ClaimType.EDUCATION)
                                        .extractedValue(qualification)
                                        .priority(ClaimPriority.PREFERRED)
                                        .sourceDocumentId(sourceDocumentId)
                                        .build()
                        )
                );

        return claims;
    }
}