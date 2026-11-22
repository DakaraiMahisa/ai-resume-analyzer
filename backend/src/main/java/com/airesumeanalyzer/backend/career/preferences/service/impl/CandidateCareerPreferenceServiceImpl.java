package com.airesumeanalyzer.backend.career.preferences.service.impl;



import com.airesumeanalyzer.backend.auth.security.CurrentUserPrincipal;
import com.airesumeanalyzer.backend.career.preferences.entity.CandidateCareerPreference;
import com.airesumeanalyzer.backend.career.preferences.dto.request.CreateCandidateCareerPreferenceRequest;
import com.airesumeanalyzer.backend.career.preferences.dto.request.UpdateCandidateCareerPreferenceRequest;
import com.airesumeanalyzer.backend.career.preferences.dto.response.CandidateCareerPreferenceResponse;

import com.airesumeanalyzer.backend.career.preferences.enums.SalaryPeriod;
import com.airesumeanalyzer.backend.career.preferences.repository.CandidateCareerPreferenceRepository;
import com.airesumeanalyzer.backend.career.preferences.service.CandidateCareerPreferenceService;
import com.airesumeanalyzer.backend.common.exception.base.BadRequestException;
import com.airesumeanalyzer.backend.common.exception.base.ConflictException;
import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import com.airesumeanalyzer.backend.user.entity.User;
import com.airesumeanalyzer.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CandidateCareerPreferenceServiceImpl
        implements CandidateCareerPreferenceService {

    private static final List<String> EMPLOYMENT_TYPES = List.of(
            "FULL_TIME",
            "PART_TIME",
            "CONTRACT",
            "INTERNSHIP",
            "TEMPORARY",
            "FREELANCE"
    );

    private static final List<String> WORK_MODES = List.of(
            "REMOTE",
            "HYBRID",
            "ONSITE"
    );

    private final CandidateCareerPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CandidateCareerPreferenceResponse create(
            CreateCandidateCareerPreferenceRequest request
    ) {
        UUID userId = getCurrentUserId();

        if (preferenceRepository.existsByUserId(userId)) {
            throw new ConflictException(
                    "Career preferences already exist for this user"
            );
        }

        validateSalaryRange(
                request.minimumSalary(),
                request.maximumSalary(),
                request.salaryCurrency(),
                request.salaryPeriod()
        );

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"
                ));

        CandidateCareerPreference preference =
                CandidateCareerPreference.builder()
                        .user(user)
                        .preferredJobTitles(normalizeList(request.preferredJobTitles()))
                        .preferredEmploymentTypes(
                                normalizeControlledList(
                                        request.preferredEmploymentTypes(),
                                        EMPLOYMENT_TYPES,
                                        "employment type"
                                )
                        )
                        .preferredWorkModes(
                                normalizeControlledList(
                                        request.preferredWorkModes(),
                                        WORK_MODES,
                                        "work mode"
                                )
                        )
                        .preferredLocations(normalizeList(request.preferredLocations()))
                        .preferredIndustries(normalizeList(request.preferredIndustries()))
                        .minimumSalary(request.minimumSalary())
                        .maximumSalary(request.maximumSalary())
                        .salaryCurrency(normalizeCurrency(request.salaryCurrency()))
                        .salaryPeriod(request.salaryPeriod())
                        .willingToRelocate(request.willingToRelocate())
                        .noticePeriodDays(request.noticePeriodDays())
                        .jobSearchStatus(request.jobSearchStatus())
                        .build();

        return toResponse(preferenceRepository.save(preference));
    }

    @Override
    public CandidateCareerPreferenceResponse getCurrentUserPreferences() {
        CandidateCareerPreference preference = getCurrentUserPreference();

        return toResponse(preference);
    }

    @Override
    @Transactional
    public CandidateCareerPreferenceResponse update(
            UpdateCandidateCareerPreferenceRequest request
    ) {
        CandidateCareerPreference preference = getCurrentUserPreference();

        if (request.preferredJobTitles() != null) {
            preference.setPreferredJobTitles(
                    normalizeList(request.preferredJobTitles())
            );
        }

        if (request.preferredEmploymentTypes() != null) {
            preference.setPreferredEmploymentTypes(
                    normalizeControlledList(
                            request.preferredEmploymentTypes(),
                            EMPLOYMENT_TYPES,
                            "employment type"
                    )
            );
        }

        if (request.preferredWorkModes() != null) {
            preference.setPreferredWorkModes(
                    normalizeControlledList(
                            request.preferredWorkModes(),
                            WORK_MODES,
                            "work mode"
                    )
            );
        }

        if (request.preferredLocations() != null) {
            preference.setPreferredLocations(
                    normalizeList(request.preferredLocations())
            );
        }

        if (request.preferredIndustries() != null) {
            preference.setPreferredIndustries(
                    normalizeList(request.preferredIndustries())
            );
        }

        updateSalaryFields(preference, request);

        if (request.jobSearchStatus() != null) {
            preference.setJobSearchStatus(request.jobSearchStatus());
        } else if (Boolean.TRUE.equals(request.clearJobSearchStatus())) {
            preference.setJobSearchStatus(null);
        }

        if (request.willingToRelocate() != null) {
            preference.setWillingToRelocate(request.willingToRelocate());
        } else if (Boolean.TRUE.equals(request.clearWillingToRelocate())) {
            preference.setWillingToRelocate(null);
        }

        if (request.noticePeriodDays() != null) {
            preference.setNoticePeriodDays(request.noticePeriodDays());
        } else if (Boolean.TRUE.equals(request.clearNoticePeriodDays())) {
            preference.setNoticePeriodDays(null);
        }

        validateSalaryRange(
                preference.getMinimumSalary(),
                preference.getMaximumSalary(),
                preference.getSalaryCurrency(),
                preference.getSalaryPeriod()
        );

        return toResponse(preferenceRepository.save(preference));
    }

    private void updateSalaryFields(
            CandidateCareerPreference preference,
            UpdateCandidateCareerPreferenceRequest request
    ) {
        if (request.minimumSalary() != null
                && Boolean.TRUE.equals(request.clearMinimumSalary())) {
            throw new BadRequestException(
                    "Cannot provide minimum salary and clear it simultaneously"
            );
        }

        if (request.maximumSalary() != null
                && Boolean.TRUE.equals(request.clearMaximumSalary())) {
            throw new BadRequestException(
                    "Cannot provide maximum salary and clear it simultaneously"
            );
        }

        if (request.salaryCurrency() != null
                && Boolean.TRUE.equals(request.clearSalaryCurrency())) {
            throw new BadRequestException(
                    "Cannot provide salary currency and clear it simultaneously"
            );
        }

        if (request.salaryPeriod() != null
                && Boolean.TRUE.equals(request.clearSalaryPeriod())) {
            throw new BadRequestException(
                    "Cannot provide salary period and clear it simultaneously"
            );
        }

        if (request.minimumSalary() != null) {
            preference.setMinimumSalary(request.minimumSalary());
        } else if (Boolean.TRUE.equals(request.clearMinimumSalary())) {
            preference.setMinimumSalary(null);
        }

        if (request.maximumSalary() != null) {
            preference.setMaximumSalary(request.maximumSalary());
        } else if (Boolean.TRUE.equals(request.clearMaximumSalary())) {
            preference.setMaximumSalary(null);
        }

        if (request.salaryCurrency() != null) {
            preference.setSalaryCurrency(
                    normalizeCurrency(request.salaryCurrency())
            );
        } else if (Boolean.TRUE.equals(request.clearSalaryCurrency())) {
            preference.setSalaryCurrency(null);
        }

        if (request.salaryPeriod() != null) {
            preference.setSalaryPeriod(request.salaryPeriod());
        } else if (Boolean.TRUE.equals(request.clearSalaryPeriod())) {
            preference.setSalaryPeriod(null);
        }
    }

    private void validateSalaryRange(
            BigDecimal minimumSalary,
            BigDecimal maximumSalary,
            String currency,
            SalaryPeriod period
    ) {
        if (minimumSalary != null
                && maximumSalary != null
                && minimumSalary.compareTo(maximumSalary) > 0) {
            throw new BadRequestException(
                    "Minimum salary cannot exceed maximum salary"
            );
        }

        if (minimumSalary != null || maximumSalary != null) {
            if (currency == null || currency.isBlank()) {
                throw new BadRequestException(
                        "Salary currency is required when a salary amount is provided"
                );
            }

            if (period == null) {
                throw new BadRequestException(
                        "Salary period is required when a salary amount is provided"
                );
            }
        }
    }

    private String normalizeCurrency(String currency) {
        if (currency == null) {
            return null;
        }

        String normalized = currency.trim().toUpperCase(Locale.ROOT);

        if (!normalized.matches("^[A-Z]{3}$")) {
            throw new BadRequestException(
                    "Salary currency must be a valid three-letter code"
            );
        }

        return normalized;
    }

    private List<String> normalizeList(List<String> values) {
        if (values == null) {
            return null;
        }

        List<String> normalized = values.stream()
                .map(String::trim)
                .toList();

        if (normalized.stream().anyMatch(String::isBlank)) {
            throw new BadRequestException(
                    "Preference lists cannot contain blank values"
            );
        }

        long distinctCount = normalized.stream()
                .map(value -> value.toLowerCase(Locale.ROOT))
                .distinct()
                .count();

        if (distinctCount != normalized.size()) {
            throw new BadRequestException(
                    "Preference lists cannot contain duplicate values"
            );
        }

        return normalized;
    }

    private List<String> normalizeControlledList(
            List<String> values,
            List<String> allowedValues,
            String fieldName
    ) {
        if (values == null) {
            return null;
        }

        List<String> normalized = values.stream()
                .map(value -> value.trim().toUpperCase(Locale.ROOT))
                .toList();

        for (String value : normalized) {
            if (!allowedValues.contains(value)) {
                throw new BadRequestException(
                        "Unsupported " + fieldName + ": " + value
                );
            }
        }

        long distinctCount = normalized.stream()
                .distinct()
                .count();

        if (distinctCount != normalized.size()) {
            throw new BadRequestException(
                    "Duplicate " + fieldName + " values are not allowed"
            );
        }

        return normalized;
    }

    private CandidateCareerPreference getCurrentUserPreference() {
        UUID userId = getCurrentUserId();

        return preferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Career preferences not found"
                ));
    }

    private UUID getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof CurrentUserPrincipal currentUser)) {
            throw new BadRequestException(
                    "Authenticated user information is unavailable"
            );
        }

        return currentUser.getUserId();
    }

    private CandidateCareerPreferenceResponse toResponse(
            CandidateCareerPreference preference
    ) {
        return new CandidateCareerPreferenceResponse(
                preference.getId(),
                preference.getPreferredJobTitles(),
                preference.getPreferredEmploymentTypes(),
                preference.getPreferredWorkModes(),
                preference.getPreferredLocations(),
                preference.getPreferredIndustries(),
                preference.getMinimumSalary(),
                preference.getMaximumSalary(),
                preference.getSalaryCurrency(),
                preference.getSalaryPeriod(),
                preference.getWillingToRelocate(),
                preference.getNoticePeriodDays(),
                preference.getJobSearchStatus(),
                preference.getCreatedAt(),
                preference.getUpdatedAt()
        );
    }
}

