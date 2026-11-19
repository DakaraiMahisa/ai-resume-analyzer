
package com.airesumeanalyzer.backend.observability.metrics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ExecutionTimeMetricsAspect {

    private final ResumeProcessingMetrics resumeProcessingMetrics;
    private final MeterRegistry meterRegistry;

    @Around("@annotation(com.airesumeanalyzer.backend.observability.metrics.TrackExecutionTime)")
    public Object measureExecutionTime(
            ProceedingJoinPoint joinPoint
    ) throws Throwable {

        TrackExecutionTime annotation;

        try {
            MethodSignature signature =
                    (MethodSignature) joinPoint.getSignature();

            Method method = AopUtils.getMostSpecificMethod(
                    signature.getMethod(),
                    joinPoint.getTarget().getClass()
            );

            annotation = AnnotatedElementUtils.findMergedAnnotation(
                    method,
                    TrackExecutionTime.class
            );

        } catch (RuntimeException exception) {
            log.warn(
                    "Unable to initialise execution-time instrumentation. " +
                            "Proceeding without metrics.",
                    exception
            );

            return joinPoint.proceed();
        }

        if (annotation == null) {
            log.warn(
                    "Execution-time annotation unavailable. " +
                            "Proceeding without metrics."
            );

            return joinPoint.proceed();
        }

        long startTime = System.nanoTime();
        String outcome = "failure";

        try {
            Object result = joinPoint.proceed();
            outcome = "success";
            return result;
        } finally {
            recordOutcome(outcome);
            recordExecutionTime(annotation, outcome, startTime);
        }
    }

    private void recordOutcome(String outcome) {
        try {
            if ("success".equals(outcome)) {
                resumeProcessingMetrics.recordSuccess();
            } else {
                resumeProcessingMetrics.recordFailure();
            }
        } catch (RuntimeException exception) {
            log.warn(
                    "Unable to record resume processing outcome metric.",
                    exception
            );
        }
    }

    private void recordExecutionTime(
            TrackExecutionTime annotation,
            String outcome,
            long startTime
    ) {
        try {
            Timer.builder(annotation.value())
                    .description(annotation.description())
                    .tag("outcome", outcome)
                    .register(meterRegistry)
                    .record(
                            System.nanoTime() - startTime,
                            TimeUnit.NANOSECONDS
                    );
        } catch (RuntimeException exception) {
            log.warn(
                    "Unable to record execution time metric. metricName={}",
                    annotation.value(),
                    exception
            );
        }
    }
}

