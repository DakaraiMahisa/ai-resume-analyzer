package com.airesumeanalyzer.backend.career.experience.service;

import com.airesumeanalyzer.backend.career.experience.dto.request.CreateProfessionalExperienceRequest;
import com.airesumeanalyzer.backend.career.experience.dto.request.UpdateProfessionalExperienceRequest;
import com.airesumeanalyzer.backend.career.experience.dto.response.ProfessionalExperienceResponse;

import java.util.List;
import java.util.UUID;

public interface ProfessionalExperienceService {

    ProfessionalExperienceResponse createExperience(
            UUID userId,
            CreateProfessionalExperienceRequest request
    );

    List<ProfessionalExperienceResponse> getExperiences(UUID userId);

    ProfessionalExperienceResponse getExperience(UUID userId, UUID experienceId);

    ProfessionalExperienceResponse updateExperience(
            UUID userId,
            UUID experienceId,
            UpdateProfessionalExperienceRequest request
    );

    void deleteExperience(UUID userId, UUID experienceId);
}