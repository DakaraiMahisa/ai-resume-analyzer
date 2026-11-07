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

            log.info(
                    "Analysis worker started. analysisId={}, traceId={}",
                    analysisId, traceId
            );

            execute(analysisId);

            log.info(
                    "Analysis worker completed successfully. analysisId={}, traceId={}",
                    analysisId, traceId
            );

        } catch (Exception exception) {

            log.error(
                    "Analysis execution failed. analysisId={}, traceId={}",
                    analysisId, traceId, exception
            );

            AnalysisFailureClassifier.FailureClassification failure =
                    failureClassifier.classify(exception);

            log.warn(
                    "Analysis failure classified. analysisId={}, traceId={}, failureCode={}, retryable={}",
                    analysisId,
                    traceId,
                    failure.code(),
                    failure.retryable()
            );

            try {
                persistenceService.markFailed(
                        analysisId,
                        failure.code(),
                        failure.message(),
                        failure.retryable(),
                        traceId
                );

                log.info(
                        "Analysis failure persisted. analysisId={}, traceId={}, failureCode={}",
                        analysisId,
                        traceId,
                        failure.code()
                );

            } catch (Exception persistenceException) {

                log.error(
                        "Unable to persist analysis failure. analysisId={}, traceId={}",
                        analysisId,
                        traceId,
                        persistenceException
                );
            }
        }
    }

    private void execute(UUID analysisId) {

        log.debug(
                "Loading analysis inputs. analysisId={}",
                analysisId
        );

        AnalysisPersistenceService.AnalysisInputs inputs =
                persistenceService.loadInputs(analysisId);

        log.info(
                "Analysis inputs loaded. analysisId={}, resumeId={}, jobDescriptionId={}",
                analysisId,
                inputs.resumeId(),
                inputs.jobDescriptionId()
        );

        log.info(
                "Starting resume-job requirement matching. analysisId={}",
                analysisId
        );

        List<RequirementMatchResult> requirementResults =
                resumeJobMatchingService.match(
                        inputs.resumeId(),
                        inputs.jobDescriptionId()
                );

        log.info(
                "Resume-job requirement matching completed. analysisId={}, requirementCount={}",
                analysisId,
                requirementResults.size()
        );

        log.info(
                "Starting ATS scoring. analysisId={}",
                analysisId
        );

        ATSResult atsResult = atsScoringService.score(requirementResults);

        log.info(
                "ATS scoring completed. analysisId={}, overallScore={}",
                analysisId,
                atsResult.overallScore()
        );

        log.info(
                "Generating analysis recommendations. analysisId={}",
                analysisId
        );

        RecommendationResponse recommendation =
                recommendationService.recommend(atsResult);

        log.info(
                "Analysis recommendations generated. analysisId={}",
                analysisId
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

        log.debug(
                "Serializing analysis result. analysisId={}",
                analysisId
        );

        String resultData = serializer.serialize(response);

        log.info(
                "Persisting completed analysis. analysisId={}",
                analysisId
        );

        persistenceService.markCompleted(
                analysisId,
                atsResult.overallScore(),
                atsResult.required().score(),
                atsResult.preferred().score(),
                resultData
        );

        log.info(
                "Analysis result persisted successfully. analysisId={}",
                analysisId
        );
    }
}

