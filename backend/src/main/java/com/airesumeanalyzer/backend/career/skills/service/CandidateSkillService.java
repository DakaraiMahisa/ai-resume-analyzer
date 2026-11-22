package com.airesumeanalyzer.backend.career.skills.service;

import com.airesumeanalyzer.backend.career.skills.dto.request.CreateCandidateSkillRequest;
import com.airesumeanalyzer.backend.career.skills.dto.request.UpdateCandidateSkillRequest;
import com.airesumeanalyzer.backend.career.skills.dto.response.CandidateSkillResponse;

import java.util.List;
import java.util.UUID;

public interface CandidateSkillService {

    CandidateSkillResponse createSkill(
            UUID userId,
            CreateCandidateSkillRequest request
    );

    List<CandidateSkillResponse> getSkills(UUID userId);

    CandidateSkillResponse getSkill(UUID userId, UUID skillId);

    CandidateSkillResponse updateSkill(
            UUID userId,
            UUID skillId,
            UpdateCandidateSkillRequest request
    );

    void deleteSkill(UUID userId, UUID skillId);
}