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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
@Slf4j
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
        log.info(
                "Starting resume analysis. ownerId={}, resumeId={}, jobDescriptionId={}",
                ownerId, resumeId, jobDescriptionId
        );

        Resume resume = resumeRepository.findByIdAndOwnerId(resumeId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resume or job description was not found"
                ));
        JobDescription jobDescription = jobDescriptionRepository
                .findByIdAndOwnerId(jobDescriptionId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resume or job description was not found"
                ));

        log.debug(
                "Resume and job description retrieved successfully. resumeId={}, jobDescriptionId={}",
                resumeId, jobDescriptionId
        );

        log.debug("Validating analysis quota. ownerId={}", ownerId);
        quotaService.assertAvailable(ownerId);

        log.debug("Analysis quota validated. ownerId={}", ownerId);

        Analysis analysis = persistenceService.createRunning(
                resume,
                jobDescription
        );

        UUID analysisId = analysis.getId();

        log.info(
                "Analysis record created. analysisId={}, resumeId={}, jobDescriptionId={}",
                analysisId, resumeId, jobDescriptionId
        );

        log.info(
                "Dispatching asynchronous analysis. analysisId={}",
                analysisId
        );
        workerService.executeAsync(analysis.getId());

        log.info(
                "Asynchronous analysis dispatch completed. analysisId={}",
                analysisId
        );
        return queryService.toStatusResponse(analysis);
    }

    @Transactional
    public AnalysisStatusResponse retry(UUID ownerId, UUID analysisId) {

        log.info(
                "Retry requested for analysis. ownerId={}, analysisId={}",
                ownerId, analysisId
        );

        Analysis previous = analysisRepository
                .findByIdAndResumeOwnerId(analysisId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Analysis not found"
                ));

        if (previous.getStatus() != AnalysisStatus.FAILED
                || !previous.isRetryable()) {

            log.warn(
                    "Analysis retry rejected. analysisId={}, status={}, retryable={}",
                    analysisId,
                    previous.getStatus(),
                    previous.isRetryable()
            );

            throw new ConflictException(
                    "Only retryable failed analyses can be retried."
            );
        }

        log.debug("Validating quota for analysis retry. ownerId={}", ownerId);
        quotaService.assertAvailable(ownerId);

        Analysis retry = persistenceService.createRetry(
                previous,
                previous.getResume(),
                previous.getJobDescription()
        );

        UUID retryAnalysisId = retry.getId();

        log.info(
                "Retry analysis record created. previousAnalysisId={}, retryAnalysisId={}, attemptNumber={}",
                analysisId,
                retryAnalysisId,
                retry.getAttemptNumber()
        );

        log.info(
                "Dispatching retry asynchronously. analysisId={}",
                retryAnalysisId
        );

        workerService.executeAsync(retry.getId());
        return queryService.toStatusResponse(retry);
    }

    @Transactional
    public void delete(UUID ownerId, UUID analysisId) {

        log.info(
                "Analysis deletion requested. ownerId={}, analysisId={}",
                ownerId, analysisId
        );

        Analysis analysis = analysisRepository
                .findByIdAndResumeOwnerId(analysisId, ownerId)
                .orElseThrow(() ->{
                    log.warn(
                            "Analysis not found or access denied. ownerId={}, analysisId={}",
                            ownerId, analysisId
                    );
                    return new ResourceNotFoundException(
                            "Analysis not found"
                    );
                });

        if (analysis.getStatus() == AnalysisStatus.RUNNING) {

            log.warn(
                    "Analysis deletion rejected because analysis is running. analysisId={}",
                    analysisId
            );
            throw new ConflictException(
                    "Running analyses cannot be deleted."
            );
        }

        analysisRepository.delete(analysis);

        log.info(
                "Analysis deleted successfully. analysisId={}, ownerId={}",
                analysisId, ownerId
        );
    }
}
