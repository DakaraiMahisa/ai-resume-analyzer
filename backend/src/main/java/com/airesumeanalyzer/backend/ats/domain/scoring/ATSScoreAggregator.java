package com.airesumeanalyzer.backend.ats.domain.scoring;

import com.airesumeanalyzer.backend.ats.domain.model.ATSRequirementScore;
import com.airesumeanalyzer.backend.ats.domain.model.ATSResult;

import java.util.List;

public interface ATSScoreAggregator {

    ATSResult aggregate(
            List<ATSRequirementScore> requirementScores
    );
}

