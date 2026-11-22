package com.airesumeanalyzer.backend.career.projects.service;

import com.airesumeanalyzer.backend.career.projects.dto.request.CreateCandidateProjectRequest;
import com.airesumeanalyzer.backend.career.projects.dto.request.UpdateCandidateProjectRequest;
import com.airesumeanalyzer.backend.career.projects.dto.response.CandidateProjectResponse;

import java.util.List;
import java.util.UUID;

public interface CandidateProjectService {

    CandidateProjectResponse createProject(
            UUID userId,
            CreateCandidateProjectRequest request
    );

    List<CandidateProjectResponse> getProjects(UUID userId);

    CandidateProjectResponse getProject(UUID userId, UUID projectId);

    CandidateProjectResponse updateProject(
            UUID userId,
            UUID projectId,
            UpdateCandidateProjectRequest request
    );

    void deleteProject(UUID userId, UUID projectId);
}