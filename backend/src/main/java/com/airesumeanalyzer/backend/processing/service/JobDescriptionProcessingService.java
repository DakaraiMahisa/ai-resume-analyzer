package com.airesumeanalyzer.backend.processing.service;

import com.airesumeanalyzer.backend.ai.claimresolution.ClaimPersistenceService;
import com.airesumeanalyzer.backend.ai.claimresolution.ClaimResolutionEngine;
import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.ai.evidence.EvidenceEnricher;
import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import com.airesumeanalyzer.backend.ai.model.understanding.StructuredJobDescription;
import com.airesumeanalyzer.backend.ai.model.understanding.StructuredResume;
import com.airesumeanalyzer.backend.ai.parser.AiResponseParser;
import com.airesumeanalyzer.backend.ai.service.JobDescriptionUnderstandingService;
import com.airesumeanalyzer.backend.ai.validation.ValidationEngine;
import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import com.airesumeanalyzer.backend.common.storage.DocumentStorage;
import com.airesumeanalyzer.backend.jobdescription.entity.JobDescription;
import com.airesumeanalyzer.backend.jobdescription.repository.JobDescriptionRepository;
import com.airesumeanalyzer.backend.processing.enums.DocumentType;
import com.airesumeanalyzer.backend.processing.exception.ProcessingException;
import com.airesumeanalyzer.backend.processing.extraction.TextExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobDescriptionProcessingService {

    private final JobDescriptionRepository jobDescriptionRepository;
    private final DocumentStorage documentStorage;
    private final TextExtractor textExtractor;
    private final JobDescriptionUnderstandingService jobDescriptionUnderstandingService;
    private final ObjectMapper objectMapper;

    private final AiResponseParser aiResponseParser;
    private final EvidenceEnricher evidenceEnricher;
    private final ValidationEngine validationEngine;
    private final ClaimResolutionEngine claimResolutionEngine;
    private final ClaimPersistenceService claimPersistenceService;


    @Transactional
    public void processJobDescription(UUID jobDescriptionId) {
        log.info(
                "JD processing started. jobDescriptionId={}",
                jobDescriptionId
        );

        JobDescription jobDescription =
                jobDescriptionRepository.findById(jobDescriptionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job description not found."
                                )
                        );
        log.info(
                "JD record loaded. jobDescriptionId={}, storagePathPresent={}",
                jobDescriptionId,
                jobDescription.getStoragePath() != null
                        && !jobDescription.getStoragePath().isBlank()
        );

        try (InputStream inputStream =
                     documentStorage.load(
                             jobDescription.getStoragePath()
                     )) {

            String extractedText =
                    textExtractor.extract(inputStream);
            log.info(
                    "JD text extracted. jobDescriptionId={}, characterCount={}",
                    jobDescriptionId,
                    extractedText == null ? 0 : extractedText.length()
            );

            jobDescription.setRawText(extractedText);

            StructuredJobDescription structuredJobDescription =
                    jobDescriptionUnderstandingService.understand(extractedText);
            log.info(
                    "JD AI structuring completed. jobDescriptionId={}, "
                            + "requiredSkills={}, preferredSkills={}, "
                            + "requiredExperience={}, preferredExperience={}, "
                            + "requiredQualifications={}, "
                            + "preferredQualifications={}, responsibilities={}",
                    jobDescriptionId,
                    sizeOf(structuredJobDescription.requiredSkills()),
                    sizeOf(structuredJobDescription.preferredSkills()),
                    sizeOf(structuredJobDescription.requiredExperience()),
                    sizeOf(structuredJobDescription.preferredExperience()),
                    sizeOf(structuredJobDescription.requiredQualifications()),
                    sizeOf(structuredJobDescription.preferredQualifications()),
                    sizeOf(structuredJobDescription.responsibilities())
            );

            jobDescription.setStructuredData(
                    objectMapper.writeValueAsString(structuredJobDescription)
            );

            // 1. AI Structured Data → Proposed Claims
            List<ProposedClaim> proposedClaims =
                    aiResponseParser.parseJobDescription(
                            structuredJobDescription,
                            jobDescription.getId()
                    );
            log.info(
                    "JD claims parsed from structured data. "
                            + "jobDescriptionId={}, proposedClaimCount={}",
                    jobDescriptionId,
                    proposedClaims.size()
            );


            List<ProposedClaim> enrichedClaims =
                    evidenceEnricher.enrich(
                            proposedClaims,
                            extractedText
                    );
            log.info(
                    "JD claim evidence enrichment completed. "
                            + "jobDescriptionId={}, enrichedClaimCount={}, "
                            + "claimsWithEvidence={}",
                    jobDescriptionId,
                    enrichedClaims.size(),
                    enrichedClaims.stream()
                            .filter(claim -> claim.evidence() != null
                                    && !claim.evidence().isEmpty())
                            .count()
            );

            List<ProposedClaim> validClaims = new java.util.ArrayList<>();
            int rejectedClaimCount = 0;
            for (ProposedClaim claim : enrichedClaims) {
                var validation = validationEngine.validate(claim);
                if (validation.valid()) {
                    validClaims.add(claim);
                    log.debug(
                            "JD claim accepted. jobDescriptionId={}, "
                                    + "claimType={}, priority={}, value={}",
                            jobDescriptionId,
                            claim.claimType(),
                            claim.priority(),
                            claim.extractedValue()
                    );
                } else {
                    rejectedClaimCount++;
                    log.warn(
                            "JD claim rejected. jobDescriptionId={}, "
                                    + "claimType={}, priority={}, value={}, "
                                    + "validator={}, reason={}",
                            jobDescriptionId,
                            claim.claimType(),
                            claim.priority(),
                            claim.extractedValue(),
                            validation.validator(),
                            validation.message()
                    );
                }
            }
            log.info(
                    "JD claim validation completed. jobDescriptionId={}, "
                            + "accepted={}, rejected={}",
                    jobDescriptionId,
                    validClaims.size(),
                    rejectedClaimCount
            );


            List<Claim> resolvedClaims =
                    claimResolutionEngine.resolve(
                            validClaims,
                            jobDescription.getId(),
                            DocumentType.JOB_DESCRIPTION
                    );
            log.info(
                    "JD claims resolved for persistence. jobDescriptionId={}, "
                            + "resolvedClaimCount={}",
                    jobDescriptionId,
                    resolvedClaims.size()
            );

            List<Claim> persistedClaims = claimPersistenceService.saveAll(
                    resolvedClaims
            );
            log.info(
                    "JD processing completed. jobDescriptionId={}, "
                            + "persistedClaimCount={}, persistedClaimTypes={}",
                    jobDescriptionId,
                    persistedClaims.size(),
                    persistedClaims.stream()
                            .collect(java.util.stream.Collectors.groupingBy(
                                    Claim::getClaimType,
                                    java.util.stream.Collectors.counting()
                            ))
            );

        } catch (IOException exception) {
            log.error(
                    "JD processing failed while reading storage. "
                            + "jobDescriptionId={}",
                    jobDescriptionId,
                    exception
            );
            throw new ProcessingException(
                    "Unable to read the stored job description.",
                    exception
            );
        }
    }

    private int sizeOf(List<?> values) {
        return values == null ? 0 : values.size();
    }
}