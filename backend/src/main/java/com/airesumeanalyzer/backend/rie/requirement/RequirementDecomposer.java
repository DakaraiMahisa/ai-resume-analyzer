package com.airesumeanalyzer.backend.rie.requirement;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.rie.domain.RequirementExpression;

public interface RequirementDecomposer {

    RequirementExpression decompose(
            Claim requirement
    );
}
