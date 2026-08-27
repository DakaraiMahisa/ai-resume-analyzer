package com.airesumeanalyzer.backend.jobdescription.entity;

import com.airesumeanalyzer.backend.common.entity.Document;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "job_descriptions")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class JobDescription extends Document {

    @Column(
            name = "original_filename",
            length = 255
    )
    private String originalFilename;

    @Column(
            name = "storage_path",
            length = 500
    )
    private String storagePath;
}
