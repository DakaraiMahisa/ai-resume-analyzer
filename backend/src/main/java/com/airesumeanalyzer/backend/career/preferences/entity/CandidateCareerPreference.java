package com.airesumeanalyzer.backend.career.preferences.entity;

import com.airesumeanalyzer.backend.career.preferences.enums.JobSearchStatus;
import com.airesumeanalyzer.backend.career.preferences.enums.SalaryPeriod;
import com.airesumeanalyzer.backend.common.entity.BaseEntity;
import com.airesumeanalyzer.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(
        name = "candidate_career_preferences",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_candidate_career_preferences_user",
                        columnNames = "user_id"
                )
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateCareerPreference extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_candidate_career_preferences_user")
    )
    private User user;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferred_job_titles", columnDefinition = "JSON")
    private List<String> preferredJobTitles;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferred_employment_types", columnDefinition = "JSON")
    private List<String> preferredEmploymentTypes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferred_work_modes", columnDefinition = "JSON")
    private List<String> preferredWorkModes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferred_locations", columnDefinition = "JSON")
    private List<String> preferredLocations;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferred_industries", columnDefinition = "JSON")
    private List<String> preferredIndustries;

    @Column(name = "minimum_salary", precision = 12, scale = 2)
    private BigDecimal minimumSalary;

    @Column(name = "maximum_salary", precision = 12, scale = 2)
    private BigDecimal maximumSalary;

    @Column(name = "salary_currency", length = 3)
    private String salaryCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "salary_period", length = 20)
    private SalaryPeriod salaryPeriod;

    @Column(name = "willing_to_relocate")
    private Boolean willingToRelocate;

    @Column(name = "notice_period_days")
    private Integer noticePeriodDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_search_status", length = 30)
    private JobSearchStatus jobSearchStatus;
}