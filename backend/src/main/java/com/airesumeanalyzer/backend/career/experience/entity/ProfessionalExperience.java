package com.airesumeanalyzer.backend.career.experience.entity;

import com.airesumeanalyzer.backend.career.experience.enums.EmploymentType;
import com.airesumeanalyzer.backend.common.entity.BaseEntity;
import com.airesumeanalyzer.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(
        name = "professional_experiences",
        indexes = {
                @Index(
                        name = "idx_professional_experiences_user",
                        columnList = "user_id"
                )
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalExperience extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_professional_experiences_user")
    )
    private User user;

    @Column(name = "company_name", nullable = false, length = 255)
    private String companyName;

    @Column(name = "job_title", nullable = false, length = 255)
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", length = 30)
    private EmploymentType employmentType;

    @Column(length = 255)
    private String location;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "currently_working", nullable = false)
    @Builder.Default
    private boolean currentlyWorking = false;

    @Column(columnDefinition = "TEXT")
    private String description;
}