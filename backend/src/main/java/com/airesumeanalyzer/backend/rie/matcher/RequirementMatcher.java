package com.airesumeanalyzer.backend.rie.matcher;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.rie.domain.RequirementExpression;
import com.airesumeanalyzer.backend.rie.domain.RequirementMatch;

import java.util.List;

public interface RequirementMatcher {

    List<RequirementMatch> match(
            RequirementExpression requirement,
            List<Claim> resumeClaims
    );
}