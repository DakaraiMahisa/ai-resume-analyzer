package com.airesumeanalyzer.backend.career.experience.dto.request;


import com.airesumeanalyzer.backend.career.experience.enums.EmploymentType;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateProfessionalExperienceRequest(

        @Size(max = 255)
        String companyName,

        @Size(max = 255)
        String jobTitle,

        EmploymentType employmentType,

        @Size(max = 255)
        String location,

        LocalDate startDate,

        LocalDate endDate,

        Boolean currentlyWorking,

        @Size(max = 10000)
        String description

) {}