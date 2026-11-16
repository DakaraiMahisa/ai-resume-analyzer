package com.airesumeanalyzer.backend.observability.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResumeProcessingMetrics {

    private final MeterRegistry meterRegistry;

    public void recordSuccess() {
        meterRegistry.counter(
                "rolenexa.resume.processing",
                "outcome",
                "success"
        ).increment();
    }

    public void recordFailure() {
        meterRegistry.counter(
                "rolenexa.resume.processing",
                "outcome",
                "failure"
        ).increment();
    }
}
