package com.airesumeanalyzer.backend.processing.service;

import com.airesumeanalyzer.backend.ai.claimresolution.ClaimPersistenceService;
import com.airesumeanalyzer.backend.ai.claimresolution.ClaimResolutionEngine;
import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.ai.evidence.EvidenceEnricher;
import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import com.airesumeanalyzer.backend.ai.model.understanding.StructuredResume;
import com.airesumeanalyzer.backend.ai.parser.AiResponseParser;
import com.airesumeanalyzer.backend.ai.service.ResumeUnderstandingService;
import com.airesumeanalyzer.backend.ai.validation.ValidationEngine;
import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import com.airesumeanalyzer.backend.common.storage.DocumentStorage;
import com.airesumeanalyzer.backend.processing.enums.DocumentType;
import com.airesumeanalyzer.backend.processing.exception.ProcessingException;
import com.airesumeanalyzer.backend.processing.extraction.TextExtractor;
import com.airesumeanalyzer.backend.resume.entity.Resume;
import com.airesumeanalyzer.backend.resume.repository.ResumeRepository;
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
public class ResumeProcessingService {

    private final ResumeRepository resumeRepository;
    private final DocumentStorage documentStorage;
    private final TextExtractor textExtractor;
    private final ResumeUnderstandingService resumeUnderstandingService;
    private final ObjectMapper objectMapper;

    private final AiResponseParser aiResponseParser;
    private final EvidenceEnricher evidenceEnricher;
    private final ValidationEngine validationEngine;
    private final ClaimResolutionEngine claimResolutionEngine;
    private final ClaimPersistenceService claimPersistenceService;


    @Transactional
    public void processResume(UUID resumeId) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resume not found."
                        )
                );

        try (InputStream inputStream =
                     documentStorage.load(resume.getStoragePath())) {

            String extractedText =
                    textExtractor.extract(inputStream);

            resume.setRawText(extractedText);
            StructuredResume structuredResume =
                    resumeUnderstandingService.understand(extractedText);

            resume.setStructuredData(
                    objectMapper.writeValueAsString(structuredResume)
            );

            List<ProposedClaim> proposedClaims =
                    aiResponseParser.parseResume(
                            structuredResume,
                            resume.getId()
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
                            resume.getId(),
                            DocumentType.RESUME
                    );

            claimPersistenceService.saveAll(resolvedClaims);

        } catch (IOException exception) {
            throw new ProcessingException(
                    "Unable to read the stored resume.",
                    exception
            );
        }
    }
}
