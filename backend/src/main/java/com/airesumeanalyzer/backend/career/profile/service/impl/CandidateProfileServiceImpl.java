package com.airesumeanalyzer.backend.career.profile.service.impl;

import com.airesumeanalyzer.backend.career.profile.dto.request.CreateCandidateProfileRequest;
import com.airesumeanalyzer.backend.career.profile.dto.request.UpdateCandidateProfileRequest;
import com.airesumeanalyzer.backend.career.profile.dto.response.CandidateProfileCompletionResponse;
import com.airesumeanalyzer.backend.career.profile.dto.response.CandidateProfileResponse;
import com.airesumeanalyzer.backend.career.profile.entity.CandidateProfile;
import com.airesumeanalyzer.backend.career.profile.repository.CandidateProfileRepository;
import com.airesumeanalyzer.backend.career.profile.service.CandidateProfileService;
import com.airesumeanalyzer.backend.common.exception.base.ConflictException;
import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import com.airesumeanalyzer.backend.user.entity.User;
import com.airesumeanalyzer.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CandidateProfileServiceImpl implements CandidateProfileService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CandidateProfileResponse createProfile(
            UUID userId,
            CreateCandidateProfileRequest request
    ) {
        if (candidateProfileRepository.existsByUserId(userId)) {
            throw new ConflictException(
                    "Candidate profile already exists for this user"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + userId
                ));

        CandidateProfile profile = CandidateProfile.builder()
                .user(user)
                .professionalHeadline(request.professionalHeadline())
                .professionalSummary(request.professionalSummary())
                .phone(request.phone())
                .location(request.location())
                .linkedinUrl(request.linkedinUrl())
                .githubUrl(request.githubUrl())
                .portfolioUrl(request.portfolioUrl())
                .careerObjective(request.careerObjective())
                .build();

        CandidateProfile savedProfile =
                candidateProfileRepository.saveAndFlush(profile);

        return mapToResponse(savedProfile);
    }

    @Override
    public CandidateProfileResponse getProfile(UUID userId) {

        CandidateProfile profile = candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Candidate profile not found for user: " + userId
                ));

        return mapToResponse(profile);
    }

    @Override
    @Transactional
    public CandidateProfileResponse updateProfile(
            UUID userId,
            UpdateCandidateProfileRequest request
    ) {
        CandidateProfile profile = candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Candidate profile not found for user: " + userId
                ));

        if (request.professionalHeadline() != null) {
            profile.setProfessionalHeadline(request.professionalHeadline());
        }

        if (request.professionalSummary() != null) {
            profile.setProfessionalSummary(request.professionalSummary());
        }

        if (request.phone() != null) {
            profile.setPhone(request.phone());
        }

        if (request.location() != null) {
            profile.setLocation(request.location());
        }

        if (request.linkedinUrl() != null) {
            profile.setLinkedinUrl(request.linkedinUrl());
        }

        if (request.githubUrl() != null) {
            profile.setGithubUrl(request.githubUrl());
        }

        if (request.portfolioUrl() != null) {
            profile.setPortfolioUrl(request.portfolioUrl());
        }

        if (request.careerObjective() != null) {
            profile.setCareerObjective(request.careerObjective());
        }

        CandidateProfile updatedProfile =
                candidateProfileRepository.saveAndFlush(profile);

        return mapToResponse(updatedProfile);
    }

    @Override
    public CandidateProfileCompletionResponse getProfileCompletion(UUID userId) {

        CandidateProfile profile = candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Candidate profile not found for user: " + userId
                ));

        int completionPercentage = 0;
        int completedFields = 0;

        List<String> missingFields = new ArrayList<>();

        if (hasValue(profile.getProfessionalHeadline())) {
            completionPercentage += 10;
            completedFields++;
        } else {
            missingFields.add("professionalHeadline");
        }

        if (hasValue(profile.getProfessionalSummary())) {
            completionPercentage += 20;
            completedFields++;
        } else {
            missingFields.add("professionalSummary");
        }

        if (hasValue(profile.getPhone())) {
            completionPercentage += 10;
            completedFields++;
        } else {
            missingFields.add("phone");
        }

        if (hasValue(profile.getLocation())) {
            completionPercentage += 10;
            completedFields++;
        } else {
            missingFields.add("location");
        }

        if (hasValue(profile.getLinkedinUrl())) {
            completionPercentage += 10;
            completedFields++;
        } else {
            missingFields.add("linkedinUrl");
        }

        if (hasValue(profile.getGithubUrl())) {
            completionPercentage += 10;
            completedFields++;
        } else {
            missingFields.add("githubUrl");
        }

        if (hasValue(profile.getPortfolioUrl())) {
            completionPercentage += 10;
            completedFields++;
        } else {
            missingFields.add("portfolioUrl");
        }

        if (hasValue(profile.getCareerObjective())) {
            completionPercentage += 20;
            completedFields++;
        } else {
            missingFields.add("careerObjective");
        }

        return new CandidateProfileCompletionResponse(
                completionPercentage,
                completedFields,
                8,
                missingFields,
                completionPercentage == 100
        );
    }

    private boolean hasValue(String value) {
        return value != null && !value.isBlank();
    }

    private CandidateProfileResponse mapToResponse(CandidateProfile profile) {
        return new CandidateProfileResponse(
                profile.getId(),
                profile.getProfessionalHeadline(),
                profile.getProfessionalSummary(),
                profile.getPhone(),
                profile.getLocation(),
                profile.getLinkedinUrl(),
                profile.getGithubUrl(),
                profile.getPortfolioUrl(),
                profile.getCareerObjective(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }

}
