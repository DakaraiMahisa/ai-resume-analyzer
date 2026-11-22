package com.airesumeanalyzer.backend.career.achievements.service.impl;

import com.airesumeanalyzer.backend.career.achievements.entity.CandidateAchievement;
import com.airesumeanalyzer.backend.career.achievements.repository.CandidateAchievementRepository;
import com.airesumeanalyzer.backend.career.achievements.dto.response.CandidateAchievementResponse;
import com.airesumeanalyzer.backend.career.achievements.dto.request.CreateCandidateAchievementRequest;
import com.airesumeanalyzer.backend.career.achievements.dto.request.UpdateCandidateAchievementRequest;
import com.airesumeanalyzer.backend.career.achievements.service.CandidateAchievementService;
import com.airesumeanalyzer.backend.common.exception.base.BadRequestException;
import com.airesumeanalyzer.backend.common.exception.base.ConflictException;
import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import com.airesumeanalyzer.backend.user.entity.User;
import com.airesumeanalyzer.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CandidateAchievementServiceImpl implements CandidateAchievementService {

    private final CandidateAchievementRepository achievementRepository;
    private final UserRepository userRepository;

    @Override
    public CandidateAchievementResponse create(
            UUID userId,
            CreateCandidateAchievementRequest request
    ) {
        String achievementName = request.achievementName().trim();
        String issuingOrganization = normalizeOptionalText(request.issuingOrganization());

        validateDuplicate(
                userId,
                achievementName,
                issuingOrganization
        );

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CandidateAchievement achievement = CandidateAchievement.builder()
                .user(user)
                .achievementName(achievementName)
                .issuingOrganization(issuingOrganization)
                .achievementType(request.achievementType())
                .issueDate(request.issueDate())
                .description(normalizeOptionalText(request.description()))
                .achievementUrl(normalizeOptionalText(request.achievementUrl()))
                .build();

        return mapToResponse(achievementRepository.save(achievement));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CandidateAchievementResponse> getAll(UUID userId) {
        return achievementRepository.findAllByUserIdOrderByIssueDateDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CandidateAchievementResponse getById(UUID userId, UUID achievementId) {
        return mapToResponse(findAchievement(userId, achievementId));
    }

    @Override
    public CandidateAchievementResponse update(
            UUID userId,
            UUID achievementId,
            UpdateCandidateAchievementRequest request
    ) {
        CandidateAchievement achievement = findAchievement(userId, achievementId);

        String achievementName = request.achievementName() == null
                ? achievement.getAchievementName()
                : request.achievementName().trim();

        String issuingOrganization = request.issuingOrganization() == null
                ? achievement.getIssuingOrganization()
                : normalizeOptionalText(request.issuingOrganization());

        if (achievementName.isBlank()) {
            throw new BadRequestException("Achievement name cannot be blank");
        }

        validateDuplicateExcludingId(
                userId,
                achievementName,
                issuingOrganization,
                achievementId
        );

        achievement.setAchievementName(achievementName);
        achievement.setIssuingOrganization(issuingOrganization);

        if (request.achievementType() != null) {
            achievement.setAchievementType(request.achievementType());
        }

        if (request.issueDate() != null) {
            achievement.setIssueDate(request.issueDate());
        }

        if (request.description() != null) {
            achievement.setDescription(normalizeOptionalText(request.description()));
        }

        if (request.achievementUrl() != null) {
            achievement.setAchievementUrl(normalizeOptionalText(request.achievementUrl()));
        }

        return mapToResponse(achievementRepository.save(achievement));
    }

    @Override
    public void delete(UUID userId, UUID achievementId) {
        CandidateAchievement achievement = findAchievement(userId, achievementId);
        achievementRepository.delete(achievement);
    }

    private CandidateAchievement findAchievement(UUID userId, UUID achievementId) {
        return achievementRepository.findByIdAndUserId(achievementId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Achievement not found"
                ));
    }

    private void validateDuplicate(
            UUID userId,
            String achievementName,
            String issuingOrganization
    ) {
        if (achievementRepository.existsDuplicate(
                userId,
                achievementName,
                issuingOrganization
        )) {
            throw new ConflictException(
                    "This achievement already exists for the issuing organization"
            );
        }
    }

    private void validateDuplicateExcludingId(
            UUID userId,
            String achievementName,
            String issuingOrganization,
            UUID achievementId
    ) {
        if (achievementRepository.existsDuplicateExcludingId(
                userId,
                achievementName,
                issuingOrganization,
                achievementId
        )) {
            throw new ConflictException(
                    "This achievement already exists for the issuing organization"
            );
        }
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private CandidateAchievementResponse mapToResponse(
            CandidateAchievement achievement
    ) {
        return new CandidateAchievementResponse(
                achievement.getId(),
                achievement.getAchievementName(),
                achievement.getIssuingOrganization(),
                achievement.getAchievementType(),
                achievement.getIssueDate(),
                achievement.getDescription(),
                achievement.getAchievementUrl(),
                achievement.getCreatedAt(),
                achievement.getUpdatedAt()
        );
    }
}
