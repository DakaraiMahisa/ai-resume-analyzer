package com.airesumeanalyzer.backend.analysis.entity;


import com.airesumeanalyzer.backend.analysis.enums.AnalysisStatus;
import com.airesumeanalyzer.backend.common.entity.BaseEntity;
import com.airesumeanalyzer.backend.jobdescription.entity.JobDescription;
import com.airesumeanalyzer.backend.resume.entity.Resume;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "analyses")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Analysis extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_description_id", nullable = false)
    private JobDescription jobDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AnalysisStatus status;

    @Column(name = "overall_score", nullable = false)
    private double overallScore;

    @Column(name = "required_score", nullable = false)
    private double requiredScore;

    @Column(name = "preferred_score", nullable = false)
    private double preferredScore;

    @Column(name = "result_data", columnDefinition = "json")
    private String resultData;

    @Column(name = "completed_at")
    private Instant completedAt;


    public void markCompleted(
            double overallScore,
            double requiredScore,
            double preferredScore,
            String resultData,
            Instant completedAt
    ) {
        this.status = AnalysisStatus.COMPLETED;
        this.overallScore = overallScore;
        this.requiredScore = requiredScore;
        this.preferredScore = preferredScore;
        this.resultData = resultData;
        this.completedAt = completedAt;
    }

    public void markFailed(String errorMessage) {
        this.status = AnalysisStatus.FAILED;
        this.resultData = errorMessage;
    }


}

