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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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

        JobDescription jobDescription =
                jobDescriptionRepository.findById(jobDescriptionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job description not found."
                                )
                        );

        try (InputStream inputStream =
                     documentStorage.load(
                             jobDescription.getStoragePath()
                     )) {

            String extractedText =
                    textExtractor.extract(inputStream);

            jobDescription.setRawText(extractedText);

            StructuredJobDescription structuredJobDescription =
                    jobDescriptionUnderstandingService.understand(extractedText);

            jobDescription.setStructuredData(
                    objectMapper.writeValueAsString(structuredJobDescription)
            );

            // 1. AI Structured Data → Proposed Claims
            List<ProposedClaim> proposedClaims =
                    aiResponseParser.parseJobDescription(
                            structuredJobDescription,
                            jobDescription.getId()
                    );


            List<ProposedClaim> enrichedClaims =
                    evidenceEnricher.enrich(
                            proposedClaims,
                            extractedText
                    );


            List<ProposedClaim> validClaims =
                    enrichedClaims.stream()
                            .filter(claim ->
                                    validationEngine
                                            .validate(claim)
                                            .valid()
                            )
                            .toList();


            List<Claim> resolvedClaims =
                    claimResolutionEngine.resolve(
                            validClaims,
                            jobDescription.getId(),
                            DocumentType.JOB_DESCRIPTION
                    );

            claimPersistenceService.saveAll(
                    resolvedClaims
            );

        } catch (IOException exception) {
            throw new ProcessingException(
                    "Unable to read the stored job description.",
                    exception
            );
        }
    }
}