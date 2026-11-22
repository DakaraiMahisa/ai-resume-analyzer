package com.airesumeanalyzer.backend.career.languages.service;

import com.airesumeanalyzer.backend.career.languages.dto.response.CandidateLanguageResponse;
import com.airesumeanalyzer.backend.career.languages.dto.request.CreateCandidateLanguageRequest;
import com.airesumeanalyzer.backend.career.languages.dto.request.UpdateCandidateLanguageRequest;

import java.util.List;
import java.util.UUID;

public interface CandidateLanguageService {

    CandidateLanguageResponse create(
            UUID userId,
            CreateCandidateLanguageRequest request
    );

    List<CandidateLanguageResponse> getAll(UUID userId);

    CandidateLanguageResponse getById(UUID userId, UUID languageId);

    CandidateLanguageResponse update(
            UUID userId,
            UUID languageId,
            UpdateCandidateLanguageRequest request
    );

    void delete(UUID userId, UUID languageId);
}
