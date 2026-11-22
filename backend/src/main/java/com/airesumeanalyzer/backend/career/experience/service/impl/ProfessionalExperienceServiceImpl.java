package com.airesumeanalyzer.backend.career.experience.service.impl;

import com.airesumeanalyzer.backend.career.experience.entity.ProfessionalExperience;
import com.airesumeanalyzer.backend.career.experience.repository.ProfessionalExperienceRepository;
import com.airesumeanalyzer.backend.career.experience.dto.request.CreateProfessionalExperienceRequest;
import com.airesumeanalyzer.backend.career.experience.dto.request.UpdateProfessionalExperienceRequest;
import com.airesumeanalyzer.backend.career.experience.dto.response.ProfessionalExperienceResponse;
import com.airesumeanalyzer.backend.career.experience.service.ProfessionalExperienceService;
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
public class ProfessionalExperienceServiceImpl implements ProfessionalExperienceService {

    private final ProfessionalExperienceRepository experienceRepository;
    private final UserRepository userRepository;

    @Override
    public ProfessionalExperienceResponse createExperience(
            UUID userId,
            CreateProfessionalExperienceRequest request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + userId)
                );

        validateDates(
                request.startDate(),
                request.endDate(),
                request.currentlyWorking()
        );

        ProfessionalExperience experience = ProfessionalExperience.builder()
                .user(user)
                .companyName(request.companyName())
                .jobTitle(request.jobTitle())
                .employmentType(request.employmentType())
                .location(request.location())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .currentlyWorking(request.currentlyWorking())
                .description(request.description())
                .build();

        return mapToResponse(experienceRepository.saveAndFlush(experience));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessionalExperienceResponse> getExperiences(UUID userId) {
        return experienceRepository
                .findAllByUserIdOrderByStartDateDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProfessionalExperienceResponse getExperience(
            UUID userId,
            UUID experienceId
    ) {
        return mapToResponse(findExperience(userId, experienceId));
    }

    @Override
    public ProfessionalExperienceResponse updateExperience(
            UUID userId,
            UUID experienceId,
            UpdateProfessionalExperienceRequest request
    ) {
        ProfessionalExperience experience = findExperience(userId, experienceId);

        LocalDate startDate = request.startDate() != null
                ? request.startDate()
                : experience.getStartDate();

        boolean currentlyWorking = request.currentlyWorking() != null
                ? request.currentlyWorking()
                : experience.isCurrentlyWorking();

        if (Boolean.TRUE.equals(request.currentlyWorking())
                && request.endDate() != null) {
            throw new BadRequestException(
                    "End date must be empty for current employment"
            );
        }

        LocalDate endDate = request.endDate() != null
                ? request.endDate()
                : experience.getEndDate();

        if (currentlyWorking && request.currentlyWorking() != null) {
            endDate = null;
        }

        validateDates(startDate, endDate, currentlyWorking);

        if (request.companyName() != null) {
            if (request.companyName().isBlank()) {
                throw new BadRequestException("Company name cannot be blank");
            }
            experience.setCompanyName(request.companyName());
        }

        if (request.jobTitle() != null) {
            if (request.jobTitle().isBlank()) {
                throw new BadRequestException("Job title cannot be blank");
            }
            experience.setJobTitle(request.jobTitle());
        }

        if (request.employmentType() != null) {
            experience.setEmploymentType(request.employmentType());
        }

        if (request.location() != null) {
            experience.setLocation(request.location());
        }

        if (request.startDate() != null) {
            experience.setStartDate(startDate);
        }

        experience.setEndDate(endDate);
        experience.setCurrentlyWorking(currentlyWorking);

        if (request.description() != null) {
            experience.setDescription(request.description());
        }

        return mapToResponse(experienceRepository.saveAndFlush(experience));
    }

    @Override
    public void deleteExperience(UUID userId, UUID experienceId) {
        ProfessionalExperience experience = findExperience(userId, experienceId);
        experienceRepository.delete(experience);
    }

    private ProfessionalExperience findExperience(UUID userId, UUID experienceId) {
        return experienceRepository.findByIdAndUserId(experienceId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Professional experience not found: " + experienceId
                        )
                );
    }

    private void validateDates(
            LocalDate startDate,
            LocalDate endDate,
            boolean currentlyWorking
    ) {
        if (startDate == null) {
            throw new BadRequestException("Start date is required");
        }

        if (currentlyWorking && endDate != null) {
            throw new BadRequestException(
                    "End date must be empty for current employment"
            );
        }

        if (!currentlyWorking && endDate == null) {
            throw new BadRequestException(
                    "End date is required for completed employment"
            );
        }

        if (endDate != null && endDate.isBefore(startDate)) {
            throw new BadRequestException(
                    "End date cannot be before start date"
            );
        }
    }

    private ProfessionalExperienceResponse mapToResponse(
            ProfessionalExperience experience
    ) {
        return new ProfessionalExperienceResponse(
                experience.getId(),
                experience.getCompanyName(),
                experience.getJobTitle(),
                experience.getEmploymentType(),
                experience.getLocation(),
                experience.getStartDate(),
                experience.getEndDate(),
                experience.isCurrentlyWorking(),
                experience.getDescription(),
                experience.getCreatedAt(),
                experience.getUpdatedAt()
        );
    }
}
