package com.airesumeanalyzer.backend.observability.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResumeProcessingMetrics {

    private final MeterRegistry meterRegistry;

    @PostConstruct
    public void initializeMetrics() {
        meterRegistry.counter(
                "rolenexa.resume.processing",
                "outcome",
                "success"
        );

        meterRegistry.counter(
                "rolenexa.resume.processing",
                "outcome",
                "failure"
        );
    }

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
