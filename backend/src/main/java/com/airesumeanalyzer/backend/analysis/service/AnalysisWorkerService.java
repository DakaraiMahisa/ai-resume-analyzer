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
        log.info(
                "Analysis started. analysisId={}, traceId={}, thread={}",
                analysisId,
                traceId,
                Thread.currentThread().getName()
        );
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
            log.error(
                    "Analysis worker failed. analysisId={}, traceId={}",
                    analysisId,
                    traceId,
                    exception
            );
        }
    }

    private void execute(UUID analysisId, String traceId) {
        try {
            log.info(
                    "Loading analysis inputs. analysisId={}, traceId={}",
                    analysisId,
                    traceId
            );
            AnalysisPersistenceService.AnalysisInputs inputs =
                    persistenceService.loadInputs(analysisId);
            log.info(
                    "Analysis inputs loaded. analysisId={}, resumeId={}, "
                            + "jobDescriptionId={}, traceId={}",
                    analysisId,
                    inputs.resumeId(),
                    inputs.jobDescriptionId(),
                    traceId
            );

            log.info(
                    "Starting resume/JD requirement matching. analysisId={}, "
                            + "traceId={}",
                    analysisId,
                    traceId
            );
            List<RequirementMatchResult> requirementResults =
                    resumeJobMatchingService.match(
                            inputs.resumeId(),
                            inputs.jobDescriptionId()
                    );
            log.info(
                    "Requirement matching completed. analysisId={}, "
                            + "resultCount={}, traceId={}",
                    analysisId,
                    requirementResults.size(),
                    traceId
            );

            log.info(
                    "Starting ATS scoring. analysisId={}, traceId={}",
                    analysisId,
                    traceId
            );
            ATSResult atsResult = atsScoringService.score(requirementResults);
            log.info(
                    "ATS scoring completed. analysisId={}, overallScore={}, "
                            + "requiredScore={}, preferredScore={}, traceId={}",
                    analysisId,
                    atsResult.overallScore(),
                    atsResult.required().score(),
                    atsResult.preferred().score(),
                    traceId
            );

            log.info(
                    "Starting recommendation generation. analysisId={}, "
                            + "traceId={}",
                    analysisId,
                    traceId
            );
            RecommendationResponse recommendation =
                    recommendationService.recommend(atsResult);
            log.info(
                    "Recommendation generation completed. analysisId={}, "
                            + "traceId={}",
                    analysisId,
                    traceId
            );
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
            log.info(
                    "Serializing and persisting completed analysis. "
                            + "analysisId={}, traceId={}",
                    analysisId,
                    traceId
            );
            String resultData = serializer.serialize(response);
            persistenceService.markCompleted(
                    analysisId,
                    atsResult.overallScore(),
                    atsResult.required().score(),
                    atsResult.preferred().score(),
                    resultData
            );
            log.info(
                    "Analysis completed successfully. analysisId={}, "
                            + "traceId={}",
                    analysisId,
                    traceId
            );
        } catch (Exception exception) {
            AnalysisFailureClassifier.FailureClassification failure =
                    failureClassifier.classify(exception);
            log.error(
                    "Analysis stage failed. analysisId={}, failureCode={}, "
                            + "retryable={}, traceId={}, exceptionType={}",
                    analysisId,
                    failure.code(),
                    failure.retryable(),
                    traceId,
                    exception.getClass().getName(),
                    exception
            );
            persistenceService.markFailed(
                    analysisId,
                    failure.code(),
                    failure.message(),
                    failure.retryable(),
                    traceId
            );
            log.info(
                    "Analysis failure persisted. analysisId={}, "
                            + "failureCode={}, traceId={}",
                    analysisId,
                    failure.code(),
                    traceId
            );
        }
    }
}
