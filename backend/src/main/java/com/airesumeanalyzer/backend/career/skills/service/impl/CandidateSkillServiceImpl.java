package com.airesumeanalyzer.backend.career.skills.service.impl;

import com.airesumeanalyzer.backend.career.skills.dto.request.CreateCandidateSkillRequest;
import com.airesumeanalyzer.backend.career.skills.dto.request.UpdateCandidateSkillRequest;
import com.airesumeanalyzer.backend.career.skills.dto.response.CandidateSkillResponse;
import com.airesumeanalyzer.backend.career.skills.entity.CandidateSkill;
import com.airesumeanalyzer.backend.career.skills.enums.SkillCategory;
import com.airesumeanalyzer.backend.career.skills.repository.CandidateSkillRepository;
import com.airesumeanalyzer.backend.career.skills.service.CandidateSkillService;
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
public class CandidateSkillServiceImpl implements CandidateSkillService {

    private final CandidateSkillRepository candidateSkillRepository;
    private final UserRepository userRepository;

    @Override
    public CandidateSkillResponse createSkill(
            UUID userId,
            CreateCandidateSkillRequest request
    ) {
        String skillName = request.skillName().trim();

        if (candidateSkillRepository
                .existsByUserIdAndCategoryAndSkillNameIgnoreCase(
                        userId, request.category(), skillName
                )) {
            throw new ConflictException(
                    "This skill already exists in the selected category"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + userId)
                );

        CandidateSkill skill = CandidateSkill.builder()
                .user(user)
                .skillName(skillName)
                .category(request.category())
                .proficiencyLevel(request.proficiencyLevel())
                .yearsOfExperience(request.yearsOfExperience())
                .build();

        return mapToResponse(candidateSkillRepository.saveAndFlush(skill));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CandidateSkillResponse> getSkills(UUID userId) {
        return candidateSkillRepository
                .findAllByUserIdOrderBySkillNameAsc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CandidateSkillResponse getSkill(UUID userId, UUID skillId) {
        return mapToResponse(findSkill(userId, skillId));
    }

    @Override
    public CandidateSkillResponse updateSkill(
            UUID userId,
            UUID skillId,
            UpdateCandidateSkillRequest request
    ) {
        CandidateSkill skill = findSkill(userId, skillId);

        String skillName = request.skillName() != null
                ? request.skillName().trim()
                : skill.getSkillName();

        if (request.skillName() != null && skillName.isBlank()) {
            throw new BadRequestException("Skill name cannot be blank");
        }

        SkillCategory category = request.category() != null
                ? request.category()
                : skill.getCategory();

        if (candidateSkillRepository
                .existsByUserIdAndCategoryAndSkillNameIgnoreCaseAndIdNot(
                        userId, category, skillName, skillId
                )) {
            throw new ConflictException(
                    "This skill already exists in the selected category"
            );
        }

        if (request.proficiencyLevel() != null) {
            skill.setProficiencyLevel(request.proficiencyLevel());
        }

        if (request.skillName() != null) {
            skill.setSkillName(skillName);
        }

        if (request.category() != null) {
            skill.setCategory(category);
        }

        if (Boolean.TRUE.equals(request.clearYearsOfExperience())
                && request.yearsOfExperience() != null) {
            throw new BadRequestException(
                    "Provide years of experience or request to clear it, not both"
            );
        }

        if (Boolean.TRUE.equals(request.clearYearsOfExperience())) {
            skill.setYearsOfExperience(null);
        } else if (request.yearsOfExperience() != null) {
            skill.setYearsOfExperience(request.yearsOfExperience());
        }

        return mapToResponse(candidateSkillRepository.saveAndFlush(skill));
    }

    @Override
    public void deleteSkill(UUID userId, UUID skillId) {
        CandidateSkill skill = findSkill(userId, skillId);
        candidateSkillRepository.delete(skill);
    }

    private CandidateSkill findSkill(UUID userId, UUID skillId) {
        return candidateSkillRepository.findByIdAndUserId(skillId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Skill not found: " + skillId)
                );
    }

    private CandidateSkillResponse mapToResponse(CandidateSkill skill) {
        return new CandidateSkillResponse(
                skill.getId(),
                skill.getSkillName(),
                skill.getCategory(),
                skill.getProficiencyLevel(),
                skill.getYearsOfExperience(),
                skill.getCreatedAt(),
                skill.getUpdatedAt()
        );
    }
}