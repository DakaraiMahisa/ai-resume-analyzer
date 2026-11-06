package com.airesumeanalyzer.backend.analysis.service;

import com.airesumeanalyzer.backend.analysis.model.ResumeAnalysisResponse;
import com.airesumeanalyzer.backend.ats.domain.model.ATSResult;
import com.airesumeanalyzer.backend.ats.service.ATSScoringService;
import com.airesumeanalyzer.backend.jobdescription.dto.JobDescriptionContext;
import com.airesumeanalyzer.backend.matching.service.ResumeJobMatchingService;
import com.airesumeanalyzer.backend.recommendation.model.RecommendationResponse;
import com.airesumeanalyzer.backend.recommendation.service.RecommendationService;
import com.airesumeanalyzer.backend.rie.domain.RequirementMatchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisWorkerService {

    private final ResumeJobMatchingService resumeJobMatchingService;
    private final ATSScoringService atsScoringService;
    private final RecommendationService recommendationService;
    private final AnalysisPersistenceService persistenceService;
    private final AnalysisResultSerializer serializer;
    private final AnalysisFailureClassifier failureClassifier;

    @Async("analysisTaskExecutor")
    public void executeAsync(UUID analysisId) {
        String traceId = UUID.randomUUID().toString();

        try (MDC.MDCCloseable ignored = MDC.putCloseable("traceId", traceId)) {
            execute(analysisId, traceId);
        } catch (Exception exception) {
            AnalysisFailureClassifier.FailureClassification failure =
                    failureClassifier.classify(exception);
            try {
                persistenceService.markFailed(
                        analysisId,
                        failure.code(),
                        failure.message(),
                        failure.retryable(),
                        traceId
                );
            } catch (Exception persistenceException) {
                log.error(
                        "Unable to persist analysis worker failure. "
                                + "analysisId={}, traceId={}",
                        analysisId,
                        traceId,
                        persistenceException
                );
            }

        }
    }

    private void execute(UUID analysisId, String traceId) {
        try {

            AnalysisPersistenceService.AnalysisInputs inputs =
                    persistenceService.loadInputs(analysisId);

            List<RequirementMatchResult> requirementResults =
                    resumeJobMatchingService.match(
                            inputs.resumeId(),
                            inputs.jobDescriptionId()
                    );

            ATSResult atsResult = atsScoringService.score(requirementResults);

            RecommendationResponse recommendation =
                    recommendationService.recommend(atsResult);

            String displayName = inputs.jobDescriptionDisplayName();
            ResumeAnalysisResponse response = new ResumeAnalysisResponse(
                    analysisId,
                    inputs.resumeId(),
                    new JobDescriptionContext(
                            inputs.jobDescriptionId(),
                            displayName == null
                                    ? "Pasted job description"
                                    : displayName
                    ),
                    requirementResults,
                    atsResult,
                    recommendation
            );

            String resultData = serializer.serialize(response);
            persistenceService.markCompleted(
                    analysisId,
                    atsResult.overallScore(),
                    atsResult.required().score(),
                    atsResult.preferred().score(),
                    resultData
            );

        } catch (Exception exception) {
            AnalysisFailureClassifier.FailureClassification failure =
                    failureClassifier.classify(exception);

            persistenceService.markFailed(
                    analysisId,
                    failure.code(),
                    failure.message(),
                    failure.retryable(),
                    traceId
            );

        }
    }
}
