package com.airesumeanalyzer.backend.observability;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MdcTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {

        // Capture the submitting thread's MDC context.
        Map<String, String> contextMap = MDC.getCopyOfContextMap();

        return () -> {

            // Preserve any existing worker-thread context.
            Map<String, String> previousContext =
                    MDC.getCopyOfContextMap();

            try {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                } else {
                    MDC.clear();
                }

                runnable.run();

            } finally {
                // Prevent context leaking between pooled tasks.
                if (previousContext != null) {
                    MDC.setContextMap(previousContext);
                } else {
                    MDC.clear();
                }
            }
        };
    }
}
