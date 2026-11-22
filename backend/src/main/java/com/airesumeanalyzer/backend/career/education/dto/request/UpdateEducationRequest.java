package com.airesumeanalyzer.backend.career.education.dto.request;


import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateEducationRequest(

        @Size(max = 255)
        String institution,

        @Size(max = 150)
        String degree,

        @Size(max = 255)
        String fieldOfStudy,

        @Size(max = 255)
        String location,

        LocalDate startDate,

        LocalDate endDate,

        Boolean currentlyStudying,

        @Size(max = 100)
        String grade,

        @Size(max = 10000)
        String description

) {}
