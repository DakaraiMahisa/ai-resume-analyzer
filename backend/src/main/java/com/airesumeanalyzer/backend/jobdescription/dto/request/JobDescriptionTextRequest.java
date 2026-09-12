package com.airesumeanalyzer.backend.jobdescription.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JobDescriptionTextRequest(

        @Size(
                max = 255,
                message = "Title must not exceed 255 characters"
        )
        String title,

        @NotBlank(
                message = "Job description content is required"
        )
        String content
) {}


