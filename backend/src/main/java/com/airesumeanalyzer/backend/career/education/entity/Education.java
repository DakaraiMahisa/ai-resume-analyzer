package com.airesumeanalyzer.backend.career.education.entity;

import com.airesumeanalyzer.backend.common.entity.BaseEntity;
import com.airesumeanalyzer.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(
        name = "educations",
        indexes = {
                @Index(
                        name = "idx_educations_user",
                        columnList = "user_id"
                )
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Education extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_educations_user")
    )
    private User user;

    @Column(nullable = false, length = 255)
    private String institution;

    @Column(nullable = false, length = 150)
    private String degree;

    @Column(name = "field_of_study", length = 255)
    private String fieldOfStudy;

    @Column(length = 255)
    private String location;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "currently_studying", nullable = false)
    @Builder.Default
    private boolean currentlyStudying = false;

    @Column(length = 100)
    private String grade;

    @Column(columnDefinition = "TEXT")
    private String description;
}