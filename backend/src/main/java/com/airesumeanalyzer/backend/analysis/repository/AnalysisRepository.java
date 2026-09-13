package com.airesumeanalyzer.backend.analysis.repository;

import com.airesumeanalyzer.backend.analysis.entity.Analysis;
import com.airesumeanalyzer.backend.analysis.enums.AnalysisStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AnalysisRepository extends JpaRepository<Analysis, UUID> {

    Page<Analysis> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByStatus(AnalysisStatus status);

    @Query("""
        select avg(a.overallScore)
        from Analysis a
        where a.status = :status
    """)
    Double findAverageOverallScoreByStatus(
            @Param("status") AnalysisStatus status
    );
}