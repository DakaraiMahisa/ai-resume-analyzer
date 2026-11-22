package com.airesumeanalyzer.backend.career.certifications.service;


import com.airesumeanalyzer.backend.career.certifications.dto.response.CandidateCertificationResponse;
import com.airesumeanalyzer.backend.career.certifications.dto.request.CreateCandidateCertificationRequest;
import com.airesumeanalyzer.backend.career.certifications.dto.request.UpdateCandidateCertificationRequest;

import java.util.List;
import java.util.UUID;

public interface CandidateCertificationService {

    CandidateCertificationResponse create(
            UUID userId,
            CreateCandidateCertificationRequest request
    );

    List<CandidateCertificationResponse> getAll(UUID userId);

    CandidateCertificationResponse getById(UUID userId, UUID certificationId);

    CandidateCertificationResponse update(
            UUID userId,
            UUID certificationId,
            UpdateCandidateCertificationRequest request
    );

    void delete(UUID userId, UUID certificationId);
}