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
        log.info("Resume processing started. resumeId={}", resumeId);

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resume not found."
                        )
                );
        log.info(
                "Resume record loaded. resumeId={}, storagePathPresent={}",
                resumeId,
                resume.getStoragePath() != null
                && !resume.getStoragePath().isBlank()
        );

        try (InputStream inputStream =
                     documentStorage.load(resume.getStoragePath())) {

            String extractedText =
                    textExtractor.extract(inputStream);
            log.info(
                    "Resume text extracted. resumeId={}, characterCount={}",
                    resumeId,
                    extractedText == null ? 0 : extractedText.length()
            );

            resume.setRawText(extractedText);
            StructuredResume structuredResume =
                    resumeUnderstandingService.understand(extractedText);
            log.info(
                    "Resume AI structuring completed. resumeId={}, skills={}, "
                            + "experience={}, education={}, projects={}, "
                            + "certifications={}",
                    resumeId,
                    sizeOf(structuredResume.skills()),
                    sizeOf(structuredResume.experience()),
                    sizeOf(structuredResume.education()),
                    sizeOf(structuredResume.projects()),
                    sizeOf(structuredResume.certifications())
            );

            resume.setStructuredData(
                    objectMapper.writeValueAsString(structuredResume)
            );

            List<ProposedClaim> proposedClaims =
                    aiResponseParser.parseResume(
                            structuredResume,
                            resume.getId()
                    );
            log.info(
                    "Resume claims parsed from structured data. resumeId={}, "
                            + "proposedClaimCount={}",
                    resumeId,
                    proposedClaims.size()
            );

            List<ProposedClaim> enrichedClaims =
                    evidenceEnricher.enrich(
                            proposedClaims,
                            extractedText
                    );
            log.info(
                    "Resume claim evidence enrichment completed. resumeId={}, "
                            + "enrichedClaimCount={}, claimsWithEvidence={}",
                    resumeId,
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
                            "Resume claim accepted. resumeId={}, claimType={}, "
                                    + "priority={}, value={}",
                            resumeId,
                            claim.claimType(),
                            claim.priority(),
                            claim.extractedValue()
                    );
                } else {
                    rejectedClaimCount++;
                    log.warn(
                            "Resume claim rejected. resumeId={}, claimType={}, "
                                    + "priority={}, value={}, validator={}, "
                                    + "reason={}",
                            resumeId,
                            claim.claimType(),
                            claim.priority(),
                            claim.extractedValue(),
                            validation.validator(),
                            validation.message()
                    );
                }
            }
            log.info(
                    "Resume claim validation completed. resumeId={}, accepted={}, "
                            + "rejected={}",
                    resumeId,
                    validClaims.size(),
                    rejectedClaimCount
            );

            List<Claim> resolvedClaims =
                    claimResolutionEngine.resolve(
                            validClaims,
                            resume.getId(),
                            DocumentType.RESUME
                    );
            log.info(
                    "Resume claims resolved for persistence. resumeId={}, "
                            + "resolvedClaimCount={}",
                    resumeId,
                    resolvedClaims.size()
            );

            List<Claim> persistedClaims =
                    claimPersistenceService.saveAll(resolvedClaims);
            log.info(
                    "Resume claims prepared; transaction commit pending. "
                            + "resumeId={}, claimCount={}, claimTypes={}",
                    resumeId,
                    persistedClaims.size(),
                    persistedClaims.stream()
                            .collect(java.util.stream.Collectors.groupingBy(
                                    Claim::getClaimType,
                                    java.util.stream.Collectors.counting()
                            ))
            );

        } catch (IOException exception) {
            log.error(
                    "Resume processing failed while reading storage. "
                            + "resumeId={}",
                    resumeId,
                    exception
            );
            throw new ProcessingException(
                    "Unable to read the stored resume.",
                    exception
            );
        }
    }

    private int sizeOf(List<?> values) {
        return values == null ? 0 : values.size();
    }
}
