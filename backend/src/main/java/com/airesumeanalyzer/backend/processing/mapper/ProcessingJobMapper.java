package com.airesumeanalyzer.backend.processing.mapper;

import com.airesumeanalyzer.backend.processing.dto.ProcessingJobResponse;
import com.airesumeanalyzer.backend.processing.entity.ProcessingJob;
import org.springframework.stereotype.Component;

@Component
public class ProcessingJobMapper {

    public ProcessingJobResponse toResponse(ProcessingJob job) {

        return new ProcessingJobResponse(
                job.getId(),
                job.getDocumentId(),
                job.getDocumentType(),
                job.getStatus(),
                job.getCurrentStage(),
                job.getProgress(),
                job.getRetryCount(),
                job.getStartedAt(),
                job.getFinishedAt(),
                job.getErrorMessage(),
                job.getCreatedAt(),
                job.getUpdatedAt()
        );
    }
}

