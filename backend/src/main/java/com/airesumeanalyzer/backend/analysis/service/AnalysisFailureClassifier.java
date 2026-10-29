package com.airesumeanalyzer.backend.analysis.service;

import com.airesumeanalyzer.backend.analysis.enums.AnalysisFailureCode;
import com.airesumeanalyzer.backend.rie.requirement.RequirementClaimConsistencyException;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class AnalysisFailureClassifier {

    public FailureClassification classify(Throwable exception) {
        if (exception instanceof RequirementClaimConsistencyException) {
            return new FailureClassification(
                    AnalysisFailureCode.REQUIREMENT_DATA_INTEGRITY,
                    "This job description needs to be reprocessed before it "
                            + "can be analyzed.",
                    false
            );
        }

        String text = exception.toString().toLowerCase(Locale.ROOT);

        if (text.contains("quota")
                || text.contains("rate limit")
                || text.contains("resource exhausted")
                || text.contains("429")) {
            return new FailureClassification(
                    AnalysisFailureCode.ANALYSIS_UNAVAILABLE,
                    "We couldn't complete this analysis right now. Please try again later.",
                    true
            );
        }

        if (text.contains("timeout")
                || text.contains("timed out")) {
            return new FailureClassification(
                    AnalysisFailureCode.ANALYSIS_TIMEOUT,
                    "The analysis took too long to complete. Please try again.",
                    true
            );
        }

        return new FailureClassification(
                AnalysisFailureCode.INTERNAL_ERROR,
                "We couldn't complete this analysis. Please try again later.",
                true
        );
    }

    public record FailureClassification(
            AnalysisFailureCode code,
            String message,
            boolean retryable
    ) {
    }
}
