package com.airesumeanalyzer.backend.resume.entity;

import com.airesumeanalyzer.backend.common.entity.Document;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "resumes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_resume_owner_checksum",
                        columnNames = {
                                "owner_id",
                                "checksum"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_resume_owner",
                        columnList = "owner_id"
                )
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Resume extends Document {

    @Column(
            name = "original_filename",
            nullable = false,
            length = 255
    )
    private String originalFilename;

    @Column(
            name = "storage_path",
            nullable = false,
            length = 500
    )
    private String storagePath;
}