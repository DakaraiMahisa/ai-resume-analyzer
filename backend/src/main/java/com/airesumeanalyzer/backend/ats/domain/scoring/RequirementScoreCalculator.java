package com.airesumeanalyzer.backend.ats.domain.scoring;

import com.airesumeanalyzer.backend.ats.domain.model.ATSRequirementScore;
import com.airesumeanalyzer.backend.rie.domain.RequirementMatchResult;


public interface RequirementScoreCalculator {

    ATSRequirementScore calculate(
            RequirementMatchResult result
    );
}

