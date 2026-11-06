package com.airesumeanalyzer.backend.ai.claimresolution;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.ai.domain.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimPersistenceServiceImpl
        implements ClaimPersistenceService {

    private static final int MAX_CANONICAL_NAME_LENGTH = 1000;

    private final ClaimRepository claimRepository;

    @Override
    @Transactional
    public List<Claim> saveAll(List<Claim> claims) {

        if (claims == null || claims.isEmpty()) {
            log.warn("Claim persistence skipped because claim list is empty.");
            return List.of();
        }

        validateCanonicalNames(claims);

        List<Claim> persistedClaims = claimRepository.saveAll(claims);

        return persistedClaims;
    }

    private void validateCanonicalNames(List<Claim> claims) {
        for (Claim claim : claims) {
            if (claim == null) {
                throw new IllegalArgumentException(
                        "Claim list must not contain null elements"
                );
            }

            String canonicalName = claim.getCanonicalName();
            if (canonicalName == null || canonicalName.isBlank()) {
                throw new IllegalArgumentException(
                        "Claim canonical name must not be blank. "
                                + "claimType=" + claim.getClaimType()
                );
            }

            if (canonicalName.length() > MAX_CANONICAL_NAME_LENGTH) {
                throw new IllegalArgumentException(
                        "Claim canonical name exceeds maximum length of "
                                + MAX_CANONICAL_NAME_LENGTH
                );
            }
        }
    }
}