package com.airesumeanalyzer.backend.career.education.service;

import com.airesumeanalyzer.backend.career.education.dto.request.CreateEducationRequest;
import com.airesumeanalyzer.backend.career.education.dto.request.UpdateEducationRequest;
import com.airesumeanalyzer.backend.career.education.dto.response.EducationResponse;

import java.util.List;
import java.util.UUID;

public interface EducationService {

    EducationResponse createEducation(
            UUID userId,
            CreateEducationRequest request
    );

    List<EducationResponse> getEducations(UUID userId);

    EducationResponse getEducation(UUID userId, UUID educationId);

    EducationResponse updateEducation(
            UUID userId,
            UUID educationId,
            UpdateEducationRequest request
    );

    void deleteEducation(UUID userId, UUID educationId);
}