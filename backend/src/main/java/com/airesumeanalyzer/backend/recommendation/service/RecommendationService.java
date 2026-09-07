package com.airesumeanalyzer.backend.recommendation.service;

import com.airesumeanalyzer.backend.ats.domain.model.ATSResult;
import com.airesumeanalyzer.backend.recommendation.model.RecommendationResponse;

public interface RecommendationService {

    RecommendationResponse recommend(
            ATSResult atsResult
    );
}