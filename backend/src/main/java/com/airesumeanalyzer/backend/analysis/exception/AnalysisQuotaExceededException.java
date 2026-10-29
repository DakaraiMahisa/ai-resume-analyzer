package com.airesumeanalyzer.backend.analysis.exception;

public class AnalysisQuotaExceededException extends RuntimeException {

    public AnalysisQuotaExceededException(String message) {
        super(message);
    }
}
