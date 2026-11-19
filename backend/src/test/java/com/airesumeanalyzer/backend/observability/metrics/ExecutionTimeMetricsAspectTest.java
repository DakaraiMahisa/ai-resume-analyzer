
package com.airesumeanalyzer.backend.observability.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecutionTimeMetricsAspectTest {

    @Mock
    private ResumeProcessingMetrics resumeProcessingMetrics;

    @Mock
    private MeterRegistry meterRegistry;

    private ExecutionTimeMetricsAspect aspect;

    @BeforeEach
    void setUp() {
        aspect = new ExecutionTimeMetricsAspect(
                resumeProcessingMetrics,
                meterRegistry
        );
    }


    @Test
    void shouldRecordSuccessMetricsWhenBusinessMethodSucceeds() throws Throwable {

        TrackExecutionTime annotation = mock(TrackExecutionTime.class);

        when(annotation.value())
                .thenReturn("test.resume.processing.duration");

        when(annotation.description())
                .thenReturn("Test resume processing duration");

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);

        MethodSignature signature = mock(MethodSignature.class);

        TestProcessingService target = new TestProcessingService();

        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getTarget()).thenReturn(target);
        when(signature.getMethod()).thenReturn(
                TestProcessingService.class.getMethod("process")
        );

        when(joinPoint.proceed()).thenReturn("completed");

        try (var mockedAnnotations = mockStatic(
                org.springframework.core.annotation.AnnotatedElementUtils.class
        )) {
            mockedAnnotations.when(() ->
                    org.springframework.core.annotation.AnnotatedElementUtils
                            .findMergedAnnotation(
                                    TestProcessingService.class.getMethod("process"),
                                    TrackExecutionTime.class
                            )
            ).thenReturn(annotation);

            Object result = aspect.measureExecutionTime(joinPoint);

            assertEquals("completed", result);
        }

        verify(joinPoint, times(1)).proceed();
        verify(resumeProcessingMetrics).recordSuccess();
        verify(resumeProcessingMetrics, never()).recordFailure();
    }


    @Test
    void shouldRecordFailureAndPropagateBusinessException() throws Throwable {

        TrackExecutionTime annotation = mock(TrackExecutionTime.class);

        when(annotation.value())
                .thenReturn("test.resume.processing.duration");

        when(annotation.description())
                .thenReturn("Test resume processing duration");

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);

        TestProcessingService target = new TestProcessingService();

        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getTarget()).thenReturn(target);
        when(signature.getMethod()).thenReturn(
                TestProcessingService.class.getMethod("process")
        );

        RuntimeException businessException =
                new RuntimeException("Processing failed");

        when(joinPoint.proceed()).thenThrow(businessException);

        try (var mockedAnnotations = mockStatic(
                org.springframework.core.annotation.AnnotatedElementUtils.class
        )) {
            mockedAnnotations.when(() ->
                    org.springframework.core.annotation.AnnotatedElementUtils
                            .findMergedAnnotation(
                                    TestProcessingService.class.getMethod("process"),
                                    TrackExecutionTime.class
                            )
            ).thenReturn(annotation);

            RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> aspect.measureExecutionTime(joinPoint)
            );

            assertSame(businessException, thrown);
        }

        verify(joinPoint, times(1)).proceed();
        verify(resumeProcessingMetrics).recordFailure();
        verify(resumeProcessingMetrics, never()).recordSuccess();
    }


    @Test
    void shouldContinueBusinessProcessingWhenOutcomeMetricFails() throws Throwable {

        TrackExecutionTime annotation = mock(TrackExecutionTime.class);

        when(annotation.value())
                .thenReturn("test.resume.processing.duration");

        when(annotation.description())
                .thenReturn("Test resume processing duration");

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);

        TestProcessingService target = new TestProcessingService();

        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getTarget()).thenReturn(target);
        when(signature.getMethod()).thenReturn(
                TestProcessingService.class.getMethod("process")
        );

        when(joinPoint.proceed()).thenReturn("completed");

        doThrow(new RuntimeException("Metrics unavailable"))
                .when(resumeProcessingMetrics)
                .recordSuccess();

        try (var mockedAnnotations = mockStatic(
                org.springframework.core.annotation.AnnotatedElementUtils.class
        )) {
            mockedAnnotations.when(() ->
                    org.springframework.core.annotation.AnnotatedElementUtils
                            .findMergedAnnotation(
                                    TestProcessingService.class.getMethod("process"),
                                    TrackExecutionTime.class
                            )
            ).thenReturn(annotation);

            Object result = aspect.measureExecutionTime(joinPoint);

            assertEquals("completed", result);
        }

        verify(joinPoint, times(1)).proceed();
        verify(resumeProcessingMetrics).recordSuccess();
        verify(resumeProcessingMetrics, never()).recordFailure();
    }


    @Test
    void shouldContinueBusinessProcessingWhenAnnotationResolutionFails() throws Throwable {

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);

        TestProcessingService target = new TestProcessingService();

        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getTarget()).thenReturn(target);
        when(signature.getMethod()).thenReturn(
                TestProcessingService.class.getMethod("process")
        );

        when(joinPoint.proceed()).thenReturn("completed");

        try (var mockedAnnotations = mockStatic(
                org.springframework.core.annotation.AnnotatedElementUtils.class
        )) {
            mockedAnnotations.when(() ->
                    org.springframework.core.annotation.AnnotatedElementUtils
                            .findMergedAnnotation(
                                    TestProcessingService.class.getMethod("process"),
                                    TrackExecutionTime.class
                            )
            ).thenThrow(new IllegalStateException(
                    "Annotation resolution failed"
            ));

            Object result = aspect.measureExecutionTime(joinPoint);

            assertEquals("completed", result);
        }

        verify(joinPoint, times(1)).proceed();

        verifyNoInteractions(resumeProcessingMetrics);
        verifyNoInteractions(meterRegistry);
    }


    static class TestProcessingService {

        public String process() {
            return "completed";
        }
    }

}
