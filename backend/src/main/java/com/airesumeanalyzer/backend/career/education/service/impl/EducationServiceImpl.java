package com.airesumeanalyzer.backend.career.education.service.impl;


import com.airesumeanalyzer.backend.career.education.dto.request.CreateEducationRequest;
import com.airesumeanalyzer.backend.career.education.dto.request.UpdateEducationRequest;
import com.airesumeanalyzer.backend.career.education.dto.response.EducationResponse;
import com.airesumeanalyzer.backend.career.education.entity.Education;
import com.airesumeanalyzer.backend.career.education.repository.EducationRepository;
import com.airesumeanalyzer.backend.career.education.service.EducationService;
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
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final UserRepository userRepository;

    @Override
    public EducationResponse createEducation(
            UUID userId,
            CreateEducationRequest request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + userId)
                );

        validateDates(
                request.startDate(),
                request.endDate(),
                request.currentlyStudying()
        );

        Education education = Education.builder()
                .user(user)
                .institution(request.institution())
                .degree(request.degree())
                .fieldOfStudy(request.fieldOfStudy())
                .location(request.location())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .currentlyStudying(request.currentlyStudying())
                .grade(request.grade())
                .description(request.description())
                .build();

        return mapToResponse(educationRepository.saveAndFlush(education));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EducationResponse> getEducations(UUID userId) {
        return educationRepository
                .findAllByUserIdOrderByStartDateDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EducationResponse getEducation(UUID userId, UUID educationId) {
        return mapToResponse(findEducation(userId, educationId));
    }

    @Override
    public EducationResponse updateEducation(
            UUID userId,
            UUID educationId,
            UpdateEducationRequest request
    ) {
        Education education = findEducation(userId, educationId);

        LocalDate startDate = request.startDate() != null
                ? request.startDate()
                : education.getStartDate();

        boolean currentlyStudying = request.currentlyStudying() != null
                ? request.currentlyStudying()
                : education.isCurrentlyStudying();

        // Reject contradictory input rather than silently ignoring endDate.
        if (Boolean.TRUE.equals(request.currentlyStudying())
                && request.endDate() != null) {
            throw new BadRequestException(
                    "End date must be empty for current studies"
            );
        }

        LocalDate endDate = request.endDate() != null
                ? request.endDate()
                : education.getEndDate();

        // Explicitly changing to current studies clears the existing end date.
        if (currentlyStudying && request.currentlyStudying() != null) {
            endDate = null;
        }

        validateDates(startDate, endDate, currentlyStudying);

        if (request.institution() != null) {
            if (request.institution().isBlank()) {
                throw new BadRequestException("Institution cannot be blank");
            }
            education.setInstitution(request.institution());
        }

        if (request.degree() != null) {
            if (request.degree().isBlank()) {
                throw new BadRequestException("Degree cannot be blank");
            }
            education.setDegree(request.degree());
        }

        if (request.fieldOfStudy() != null) {
            education.setFieldOfStudy(request.fieldOfStudy());
        }

        if (request.location() != null) {
            education.setLocation(request.location());
        }

        if (request.startDate() != null) {
            education.setStartDate(startDate);
        }

        education.setEndDate(endDate);
        education.setCurrentlyStudying(currentlyStudying);

        if (request.grade() != null) {
            education.setGrade(request.grade());
        }

        if (request.description() != null) {
            education.setDescription(request.description());
        }

        return mapToResponse(educationRepository.saveAndFlush(education));
    }

    @Override
    public void deleteEducation(UUID userId, UUID educationId) {
        Education education = findEducation(userId, educationId);
        educationRepository.delete(education);
    }

    private Education findEducation(UUID userId, UUID educationId) {
        return educationRepository.findByIdAndUserId(educationId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Education record not found: " + educationId
                        )
                );
    }

    private void validateDates(
            LocalDate startDate,
            LocalDate endDate,
            boolean currentlyStudying
    ) {
        if (startDate == null) {
            throw new BadRequestException("Start date is required");
        }

        if (currentlyStudying && endDate != null) {
            throw new BadRequestException(
                    "End date must be empty for current studies"
            );
        }

        if (!currentlyStudying && endDate == null) {
            throw new BadRequestException(
                    "End date is required for completed education"
            );
        }

        if (endDate != null && endDate.isBefore(startDate)) {
            throw new BadRequestException(
                    "End date cannot be before start date"
            );
        }
    }

    private EducationResponse mapToResponse(Education education) {
        return new EducationResponse(
                education.getId(),
                education.getInstitution(),
                education.getDegree(),
                education.getFieldOfStudy(),
                education.getLocation(),
                education.getStartDate(),
                education.getEndDate(),
                education.isCurrentlyStudying(),
                education.getGrade(),
                education.getDescription(),
                education.getCreatedAt(),
                education.getUpdatedAt()
        );
    }
}