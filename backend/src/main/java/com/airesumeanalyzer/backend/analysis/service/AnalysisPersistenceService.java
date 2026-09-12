package com.airesumeanalyzer.backend.analysis.service;



import com.airesumeanalyzer.backend.analysis.entity.Analysis;
import com.airesumeanalyzer.backend.analysis.enums.AnalysisStatus;
import com.airesumeanalyzer.backend.analysis.repository.AnalysisRepository;
import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import com.airesumeanalyzer.backend.jobdescription.entity.JobDescription;
import com.airesumeanalyzer.backend.resume.entity.Resume;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalysisPersistenceService {

    private final AnalysisRepository analysisRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Analysis createRunning(
            Resume resume,
            JobDescription jobDescription
    ) {
        Analysis analysis = Analysis.builder()
                .resume(resume)
                .jobDescription(jobDescription)
                .status(AnalysisStatus.RUNNING)
                .overallScore(0.0)
                .requiredScore(0.0)
                .preferredScore(0.0)
                .build();

        return analysisRepository.save(analysis);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markCompleted(
            UUID analysisId,
            double overallScore,
            double requiredScore,
            double preferredScore,
            String resultData
    ) {
        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Analysis not found: " + analysisId
                        )
                );

        analysis.markCompleted(
                overallScore,
                requiredScore,
                preferredScore,
                resultData,
                Instant.now()
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(
            UUID analysisId,
            String errorMessage
    ) {
        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Analysis not found: " + analysisId
                        )
                );

        analysis.markFailed(errorMessage);
    }
}


