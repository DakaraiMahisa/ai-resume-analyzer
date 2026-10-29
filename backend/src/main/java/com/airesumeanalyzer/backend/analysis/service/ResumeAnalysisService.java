package com.airesumeanalyzer.backend.analysis.service;

import com.airesumeanalyzer.backend.analysis.dto.AnalysisStatusResponse;
import com.airesumeanalyzer.backend.analysis.entity.Analysis;
import com.airesumeanalyzer.backend.analysis.enums.AnalysisStatus;
import com.airesumeanalyzer.backend.analysis.repository.AnalysisRepository;
import com.airesumeanalyzer.backend.common.exception.base.ConflictException;
import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import com.airesumeanalyzer.backend.jobdescription.entity.JobDescription;
import com.airesumeanalyzer.backend.jobdescription.repository.JobDescriptionRepository;
import com.airesumeanalyzer.backend.resume.entity.Resume;
import com.airesumeanalyzer.backend.resume.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResumeAnalysisService {

    private final ResumeRepository resumeRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisPersistenceService persistenceService;
    private final AnalysisQuotaService quotaService;
    private final AnalysisWorkerService workerService;
    private final AnalysisQueryService queryService;

    @Transactional
    public AnalysisStatusResponse start(
            UUID ownerId,
            UUID resumeId,
            UUID jobDescriptionId
    ) {
        Resume resume = resumeRepository.findByIdAndOwnerId(resumeId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resume or job description was not found"
                ));
        JobDescription jobDescription = jobDescriptionRepository
                .findByIdAndOwnerId(jobDescriptionId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resume or job description was not found"
                ));

        quotaService.assertAvailable(ownerId);
        Analysis analysis = persistenceService.createRunning(
                resume,
                jobDescription
        );
        workerService.executeAsync(analysis.getId());
        return queryService.toStatusResponse(analysis);
    }

    @Transactional
    public AnalysisStatusResponse retry(UUID ownerId, UUID analysisId) {
        Analysis previous = analysisRepository
                .findByIdAndResumeOwnerId(analysisId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Analysis not found"
                ));

        if (previous.getStatus() != AnalysisStatus.FAILED
                || !previous.isRetryable()) {
            throw new ConflictException(
                    "Only retryable failed analyses can be retried."
            );
        }

        quotaService.assertAvailable(ownerId);
        Analysis retry = persistenceService.createRetry(
                previous,
                previous.getResume(),
                previous.getJobDescription()
        );
        workerService.executeAsync(retry.getId());
        return queryService.toStatusResponse(retry);
    }

    @Transactional
    public void delete(UUID ownerId, UUID analysisId) {
        Analysis analysis = analysisRepository
                .findByIdAndResumeOwnerId(analysisId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Analysis not found"
                ));

        if (analysis.getStatus() == AnalysisStatus.RUNNING) {
            throw new ConflictException(
                    "Running analyses cannot be deleted."
            );
        }

        analysisRepository.delete(analysis);
    }
}
