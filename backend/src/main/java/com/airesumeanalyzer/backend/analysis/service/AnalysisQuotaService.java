package com.airesumeanalyzer.backend.analysis.service;

import com.airesumeanalyzer.backend.analysis.exception.AnalysisQuotaExceededException;
import com.airesumeanalyzer.backend.analysis.repository.AnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalysisQuotaService {

    private final AnalysisRepository analysisRepository;

    @Value("${app.analysis.quota.daily-limit:10}")
    private long dailyLimit;

    @Transactional(readOnly = true)
    public void assertAvailable(UUID ownerId) {
        Instant startOfDay = ZonedDateTime.now(ZoneOffset.UTC)
                .toLocalDate()
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();

        long reserved = analysisRepository
                .countByResumeOwnerIdAndCreatedAtGreaterThanEqualAndQuotaReservedTrue(
                        ownerId,
                        startOfDay
                );

        if (reserved >= dailyLimit) {
            throw new AnalysisQuotaExceededException(
                    "You have reached your daily analysis limit. Please try again later."
            );
        }
    }
}
