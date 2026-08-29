package com.airesumeanalyzer.backend.ai.claimresolution;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;

import java.util.List;

public interface ClaimPersistenceService {

    List<Claim> saveAll(List<Claim> claims);
}