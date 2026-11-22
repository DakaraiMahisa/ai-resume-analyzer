package com.airesumeanalyzer.backend.career.profile.service;

import com.airesumeanalyzer.backend.career.profile.dto.request.CreateCandidateProfileRequest;
import com.airesumeanalyzer.backend.career.profile.dto.request.UpdateCandidateProfileRequest;
import com.airesumeanalyzer.backend.career.profile.dto.response.CandidateProfileCompletionResponse;
import com.airesumeanalyzer.backend.career.profile.dto.response.CandidateProfileResponse;

import java.util.UUID;

public interface CandidateProfileService {

    CandidateProfileResponse createProfile(
            UUID userId,
            CreateCandidateProfileRequest request
    );

    CandidateProfileResponse getProfile(UUID userId);

    CandidateProfileResponse updateProfile(
            UUID userId,
            UpdateCandidateProfileRequest request
    );

    CandidateProfileCompletionResponse getProfileCompletion(UUID userId);
}
