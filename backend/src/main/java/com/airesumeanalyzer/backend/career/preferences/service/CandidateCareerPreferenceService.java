package com.airesumeanalyzer.backend.career.preferences.service;


import com.airesumeanalyzer.backend.career.preferences.dto.request.CreateCandidateCareerPreferenceRequest;
import com.airesumeanalyzer.backend.career.preferences.dto.request.UpdateCandidateCareerPreferenceRequest;
import com.airesumeanalyzer.backend.career.preferences.dto.response.CandidateCareerPreferenceResponse;

public interface CandidateCareerPreferenceService {

    CandidateCareerPreferenceResponse create(
            CreateCandidateCareerPreferenceRequest request
    );

    CandidateCareerPreferenceResponse getCurrentUserPreferences();

    CandidateCareerPreferenceResponse update(
            UpdateCandidateCareerPreferenceRequest request
    );
}
