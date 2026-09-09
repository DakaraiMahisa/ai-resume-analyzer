package com.airesumeanalyzer.backend.ai.claimresolution;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.ai.domain.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimPersistenceServiceImpl
        implements ClaimPersistenceService {

    private final ClaimRepository claimRepository;

    @Override
    @Transactional
    public List<Claim> saveAll(List<Claim> claims) {

        if (claims == null || claims.isEmpty()) {
            return List.of();
        }

        return claimRepository.saveAll(claims);
    }
}