package com.airesumeanalyzer.backend.career.achievements.service;

import com.airesumeanalyzer.backend.career.achievements.dto.response.CandidateAchievementResponse;
import com.airesumeanalyzer.backend.career.achievements.dto.request.CreateCandidateAchievementRequest;
import com.airesumeanalyzer.backend.career.achievements.dto.request.UpdateCandidateAchievementRequest;

import java.util.List;
import java.util.UUID;

public interface CandidateAchievementService {

    CandidateAchievementResponse create(
            UUID userId,
            CreateCandidateAchievementRequest request
    );

    List<CandidateAchievementResponse> getAll(UUID userId);

    CandidateAchievementResponse getById(UUID userId, UUID achievementId);

    CandidateAchievementResponse update(
            UUID userId,
            UUID achievementId,
            UpdateCandidateAchievementRequest request
    );

    void delete(UUID userId, UUID achievementId);
}
