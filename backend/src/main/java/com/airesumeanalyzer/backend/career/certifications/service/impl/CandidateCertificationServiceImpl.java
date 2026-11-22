package com.airesumeanalyzer.backend.career.certifications.service.impl;


import com.airesumeanalyzer.backend.career.certifications.entity.CandidateCertification;
import com.airesumeanalyzer.backend.career.certifications.repository.CandidateCertificationRepository;
import com.airesumeanalyzer.backend.career.certifications.dto.response.CandidateCertificationResponse;
import com.airesumeanalyzer.backend.career.certifications.dto.request.CreateCandidateCertificationRequest;
import com.airesumeanalyzer.backend.career.certifications.dto.request.UpdateCandidateCertificationRequest;
import com.airesumeanalyzer.backend.career.certifications.service.CandidateCertificationService;
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
public class CandidateCertificationServiceImpl implements CandidateCertificationService {

    private final CandidateCertificationRepository certificationRepository;
    private final UserRepository userRepository;

    @Override
    public CandidateCertificationResponse create(
            UUID userId,
            CreateCandidateCertificationRequest request
    ) {
        validateDates(
                request.issueDate(),
                request.expirationDate(),
                request.doesNotExpire()
        );

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CandidateCertification certification = CandidateCertification.builder()
                .user(user)
                .certificationName(request.certificationName().trim())
                .issuingOrganization(request.issuingOrganization().trim())
                .issueDate(request.issueDate())
                .expirationDate(request.expirationDate())
                .credentialId(normalizeOptionalText(request.credentialId()))
                .credentialUrl(normalizeOptionalText(request.credentialUrl()))
                .doesNotExpire(request.doesNotExpire())
                .build();

        return mapToResponse(certificationRepository.save(certification));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CandidateCertificationResponse> getAll(UUID userId) {
        return certificationRepository.findAllByUserIdOrderByIssueDateDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CandidateCertificationResponse getById(UUID userId, UUID certificationId) {
        return mapToResponse(findCertification(userId, certificationId));
    }

    @Override
    public CandidateCertificationResponse update(
            UUID userId,
            UUID certificationId,
            UpdateCandidateCertificationRequest request
    ) {
        CandidateCertification certification = findCertification(userId, certificationId);

        String certificationName = request.certificationName() == null
                ? certification.getCertificationName()
                : request.certificationName().trim();

        String issuingOrganization = request.issuingOrganization() == null
                ? certification.getIssuingOrganization()
                : request.issuingOrganization().trim();

        LocalDate issueDate = request.issueDate() == null
                ? certification.getIssueDate()
                : request.issueDate();

        LocalDate expirationDate = request.expirationDate() == null
                ? certification.getExpirationDate()
                : request.expirationDate();

        boolean doesNotExpire = request.doesNotExpire() == null
                ? certification.isDoesNotExpire()
                : request.doesNotExpire();

        if (certificationName.isBlank()) {
            throw new BadRequestException("Certification name cannot be blank");
        }

        if (issuingOrganization.isBlank()) {
            throw new BadRequestException("Issuing organization cannot be blank");
        }

        validateDates(issueDate, expirationDate, doesNotExpire);

        certification.setCertificationName(certificationName);
        certification.setIssuingOrganization(issuingOrganization);
        certification.setIssueDate(issueDate);
        certification.setExpirationDate(expirationDate);
        certification.setCredentialId(
                request.credentialId() == null
                        ? certification.getCredentialId()
                        : normalizeOptionalText(request.credentialId())
        );
        certification.setCredentialUrl(
                request.credentialUrl() == null
                        ? certification.getCredentialUrl()
                        : normalizeOptionalText(request.credentialUrl())
        );
        certification.setDoesNotExpire(doesNotExpire);

        return mapToResponse(certificationRepository.save(certification));
    }

    @Override
    public void delete(UUID userId, UUID certificationId) {
        CandidateCertification certification = findCertification(userId, certificationId);
        certificationRepository.delete(certification);
    }

    private CandidateCertification findCertification(UUID userId, UUID certificationId) {
        return certificationRepository.findByIdAndUserId(certificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Certification not found"
                ));
    }

    private void validateDates(
            LocalDate issueDate,
            LocalDate expirationDate,
            boolean doesNotExpire
    ) {
        if (issueDate != null && expirationDate != null
                && expirationDate.isBefore(issueDate)) {
            throw new BadRequestException(
                    "Expiration date cannot be before issue date"
            );
        }

        if (doesNotExpire && expirationDate != null) {
            throw new BadRequestException(
                    "Expiration date must be empty when the certification does not expire"
            );
        }
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private CandidateCertificationResponse mapToResponse(
            CandidateCertification certification
    ) {
        return new CandidateCertificationResponse(
                certification.getId(),
                certification.getCertificationName(),
                certification.getIssuingOrganization(),
                certification.getIssueDate(),
                certification.getExpirationDate(),
                certification.getCredentialId(),
                certification.getCredentialUrl(),
                certification.isDoesNotExpire(),
                certification.getCreatedAt(),
                certification.getUpdatedAt()
        );
    }
}
