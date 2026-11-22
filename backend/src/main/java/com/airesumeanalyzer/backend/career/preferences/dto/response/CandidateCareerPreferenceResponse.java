package com.airesumeanalyzer.backend.career.preferences.dto.response;

import com.airesumeanalyzer.backend.career.preferences.enums.JobSearchStatus;
import com.airesumeanalyzer.backend.career.preferences.enums.SalaryPeriod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CandidateCareerPreferenceResponse(

        UUID id,

        List<String> preferredJobTitles,
        List<String> preferredEmploymentTypes,
        List<String> preferredWorkModes,
        List<String> preferredLocations,
        List<String> preferredIndustries,

        BigDecimal minimumSalary,
        BigDecimal maximumSalary,
        String salaryCurrency,
        SalaryPeriod salaryPeriod,

        Boolean willingToRelocate,
        Integer noticePeriodDays,
        JobSearchStatus jobSearchStatus,

        Instant createdAt,
        Instant updatedAt

) {}
