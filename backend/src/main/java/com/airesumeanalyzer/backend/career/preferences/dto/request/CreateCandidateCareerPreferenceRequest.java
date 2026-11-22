package com.airesumeanalyzer.backend.career.preferences.dto.request;

import com.airesumeanalyzer.backend.career.preferences.enums.JobSearchStatus;
import com.airesumeanalyzer.backend.career.preferences.enums.SalaryPeriod;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record CreateCandidateCareerPreferenceRequest(

        List<@NotBlank @Size(max = 255) String> preferredJobTitles,

        List<@NotBlank @Size(max = 100) String> preferredEmploymentTypes,

        List<@NotBlank @Size(max = 50) String> preferredWorkModes,

        List<@NotBlank @Size(max = 255) String> preferredLocations,

        List<@NotBlank @Size(max = 150) String> preferredIndustries,

        @DecimalMin("0.00")
        @Digits(integer = 10, fraction = 2)
        BigDecimal minimumSalary,

        @DecimalMin("0.00")
        @Digits(integer = 10, fraction = 2)
        BigDecimal maximumSalary,

        @Pattern(regexp = "^[A-Za-z]{3}$")
        String salaryCurrency,

        SalaryPeriod salaryPeriod,

        Boolean willingToRelocate,

        @Min(0)
        Integer noticePeriodDays,

        JobSearchStatus jobSearchStatus

) {}
