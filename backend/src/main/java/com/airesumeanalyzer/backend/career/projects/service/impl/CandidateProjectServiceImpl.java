package com.airesumeanalyzer.backend.career.projects.service.impl;

import com.airesumeanalyzer.backend.career.projects.dto.request.CreateCandidateProjectRequest;
import com.airesumeanalyzer.backend.career.projects.dto.request.UpdateCandidateProjectRequest;
import com.airesumeanalyzer.backend.career.projects.dto.response.CandidateProjectResponse;
import com.airesumeanalyzer.backend.career.projects.entity.CandidateProject;
import com.airesumeanalyzer.backend.career.projects.repository.CandidateProjectRepository;
import com.airesumeanalyzer.backend.career.projects.service.CandidateProjectService;
import com.airesumeanalyzer.backend.common.exception.base.BadRequestException;
import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import com.airesumeanalyzer.backend.user.entity.User;
import com.airesumeanalyzer.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CandidateProjectServiceImpl implements CandidateProjectService {

    private final CandidateProjectRepository candidateProjectRepository;
    private final UserRepository userRepository;

    @Override
    public CandidateProjectResponse createProject(
            UUID userId,
            CreateCandidateProjectRequest request
    ) {
        validateDates(
                request.startDate(),
                request.endDate(),
                request.currentlyWorking()
        );

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + userId)
                );

        CandidateProject project = CandidateProject.builder()
                .user(user)
                .projectName(request.projectName().trim())
                .role(normalizeText(request.role()))
                .description(normalizeText(request.description()))
                .technologies(normalizeTechnologies(request.technologies()))
                .projectUrl(normalizeText(request.projectUrl()))
                .startDate(request.startDate())
                .endDate(request.endDate())
                .currentlyWorking(request.currentlyWorking())
                .build();

        return mapToResponse(candidateProjectRepository.saveAndFlush(project));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CandidateProjectResponse> getProjects(UUID userId) {
        return candidateProjectRepository
                .findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CandidateProjectResponse getProject(UUID userId, UUID projectId) {
        return mapToResponse(findProject(userId, projectId));
    }

    @Override
    public CandidateProjectResponse updateProject(
            UUID userId,
            UUID projectId,
            UpdateCandidateProjectRequest request
    ) {
        CandidateProject project = findProject(userId, projectId);

        if (request.projectName() != null) {
            if (request.projectName().isBlank()) {
                throw new BadRequestException("Project name cannot be blank");
            }
            project.setProjectName(request.projectName().trim());
        }

        if (request.role() != null) {
            project.setRole(normalizeText(request.role()));
        }

        if (request.description() != null) {
            project.setDescription(normalizeText(request.description()));
        }

        if (request.technologies() != null) {
            project.setTechnologies(
                    normalizeTechnologies(request.technologies())
            );
        }

        if (request.projectUrl() != null) {
            project.setProjectUrl(normalizeText(request.projectUrl()));
        }

        LocalDate startDate = request.startDate() != null
                ? request.startDate()
                : project.getStartDate();

        LocalDate endDate = request.endDate() != null
                ? request.endDate()
                : project.getEndDate();

        boolean currentlyWorking = request.currentlyWorking() != null
                ? request.currentlyWorking()
                : project.isCurrentlyWorking();

        if (Boolean.TRUE.equals(request.currentlyWorking())
                && request.endDate() != null) {
            throw new BadRequestException(
                    "End date must be empty for ongoing projects"
            );
        }

        if (Boolean.TRUE.equals(request.currentlyWorking())) {
            endDate = null;
        }

        validateDates(startDate, endDate, currentlyWorking);

        project.setStartDate(startDate);
        project.setEndDate(endDate);
        project.setCurrentlyWorking(currentlyWorking);

        return mapToResponse(candidateProjectRepository.saveAndFlush(project));
    }

    @Override
    public void deleteProject(UUID userId, UUID projectId) {
        CandidateProject project = findProject(userId, projectId);
        candidateProjectRepository.delete(project);
    }

    private CandidateProject findProject(UUID userId, UUID projectId) {
        return candidateProjectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found: " + projectId
                        )
                );
    }

    private void validateDates(
            LocalDate startDate,
            LocalDate endDate,
            boolean currentlyWorking
    ) {
        if (currentlyWorking && endDate != null) {
            throw new BadRequestException(
                    "End date must be empty for ongoing projects"
            );
        }

        if (startDate != null && endDate != null
                && endDate.isBefore(startDate)) {
            throw new BadRequestException(
                    "End date cannot be before start date"
            );
        }
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private List<String> normalizeTechnologies(List<String> technologies) {
        if (technologies == null) {
            return null;
        }

        return technologies.stream()
                .map(String::trim)
                .toList();
    }

    private CandidateProjectResponse mapToResponse(CandidateProject project) {
        return new CandidateProjectResponse(
                project.getId(),
                project.getProjectName(),
                project.getRole(),
                project.getDescription(),
                project.getTechnologies(),
                project.getProjectUrl(),
                project.getStartDate(),
                project.getEndDate(),
                project.isCurrentlyWorking(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}