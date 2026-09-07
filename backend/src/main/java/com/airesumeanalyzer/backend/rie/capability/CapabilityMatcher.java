package com.airesumeanalyzer.backend.rie.capability;


import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.rie.domain.RequirementComponent;
import com.airesumeanalyzer.backend.rie.domain.RequirementMatch;

import java.util.Optional;

public interface CapabilityMatcher {

    Optional<RequirementMatch> match(
            RequirementComponent requirement,
            Claim resumeClaim
    );
}