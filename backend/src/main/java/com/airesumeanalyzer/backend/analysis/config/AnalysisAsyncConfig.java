package com.airesumeanalyzer.backend.analysis.config;

import com.airesumeanalyzer.backend.observability.MdcTaskDecorator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;



@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AnalysisAsyncConfig {

    private final MdcTaskDecorator mdcTaskDecorator;

    @Bean(name = "analysisTaskExecutor")
    public Executor analysisTaskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("analysis-");

        executor.setTaskDecorator(
                new ContextPropagatingTaskDecorator()
        );

        executor.initialize();

        return executor;
    }
}

