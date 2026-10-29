package com.airesumeanalyzer.backend.analysis.service;

import com.airesumeanalyzer.backend.analysis.dto.AnalysisDashboardSummaryResponse;
import com.airesumeanalyzer.backend.analysis.dto.AnalysisErrorResponse;
import com.airesumeanalyzer.backend.analysis.dto.AnalysisSummaryResponse;
import com.airesumeanalyzer.backend.analysis.dto.AnalysisStatusResponse;
import com.airesumeanalyzer.backend.analysis.entity.Analysis;
import com.airesumeanalyzer.backend.analysis.enums.AnalysisStatus;
import com.airesumeanalyzer.backend.analysis.model.ResumeAnalysisResponse;
import com.airesumeanalyzer.backend.analysis.repository.AnalysisRepository;
import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisQueryService {

    private final AnalysisRepository analysisRepository;
    private final AnalysisResultSerializer analysisResultSerializer;

    public AnalysisStatusResponse getById(UUID ownerId, UUID analysisId) {

        Objects.requireNonNull(
                analysisId,
                "analysisId must not be null"
        );

        Analysis analysis = analysisRepository
                .findByIdAndResumeOwnerId(analysisId, ownerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Analysis not found: " + analysisId
                        )
                );

        return toStatusResponse(analysis);
    }

    public AnalysisStatusResponse toStatusResponse(Analysis analysis) {
        ResumeAnalysisResponse result = null;
        AnalysisErrorResponse error = null;
        if (analysis.getStatus() == AnalysisStatus.COMPLETED
                && analysis.getResultData() != null) {
            result = analysisResultSerializer.deserialize(
                    analysis.getResultData(),
                    ResumeAnalysisResponse.class
            );
        } else if (analysis.getStatus() == AnalysisStatus.FAILED) {
            error = new AnalysisErrorResponse(
                    analysis.getFailureCode(),
                    analysis.getErrorMessage(),
                    analysis.isRetryable()
            );
        }
        return new AnalysisStatusResponse(
                analysis.getId(),
                analysis.getStatus(),
                result,
                error,
                analysis.getCreatedAt(),
                analysis.getStartedAt(),
                analysis.getCompletedAt(),
                analysis.getUpdatedAt(),
                analysis.getAttemptNumber()
        );
    }

    @Transactional(readOnly = true)
    public Page<AnalysisSummaryResponse> getAll(
            Pageable pageable
    ) {
        return analysisRepository
                .findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toSummaryResponse);
    }

    @Transactional(readOnly = true)
    public AnalysisDashboardSummaryResponse getDashboardSummary() {

        long totalAnalyses =
                analysisRepository.count();

        long completedAnalyses =
                analysisRepository.countByStatus(
                        AnalysisStatus.COMPLETED
                );

        Double average =
                analysisRepository.findAverageOverallScoreByStatus(
                        AnalysisStatus.COMPLETED
                );

        return new AnalysisDashboardSummaryResponse(
                totalAnalyses,
                completedAnalyses,
                average != null ? average : 0.0
        );
    }

    private AnalysisSummaryResponse toSummaryResponse(
            Analysis analysis
    ) {
        String resumeDisplayName =
                analysis.getResume().getOriginalFilename();

        String jobDescriptionDisplayName =
                analysis.getJobDescription().getOriginalFilename();

        if (jobDescriptionDisplayName == null) {
            jobDescriptionDisplayName =
                    "Pasted job description";
        }

        return new AnalysisSummaryResponse(
                analysis.getId(),
                analysis.getResume().getId(),
                resumeDisplayName,
                analysis.getJobDescription().getId(),
                jobDescriptionDisplayName,
                analysis.getStatus(),
                analysis.getOverallScore(),
                analysis.getCompletedAt(),
                analysis.getCreatedAt()
        );
    }
}
