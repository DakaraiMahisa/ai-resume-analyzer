package com.airesumeanalyzer.backend.recommendation.service;

import com.airesumeanalyzer.backend.ai.provider.AiModelProvider;
import com.airesumeanalyzer.backend.ats.domain.model.ATSResult;
import com.airesumeanalyzer.backend.recommendation.model.RecommendationResponse;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class RecommendationServiceImpl
        implements RecommendationService {

    private final AiModelProvider aiModelProvider;

    public RecommendationServiceImpl(
            AiModelProvider aiModelProvider
    ) {
        this.aiModelProvider =
                Objects.requireNonNull(
                        aiModelProvider,
                        "aiModelProvider must not be null"
                );
    }

    @Override
    public RecommendationResponse recommend(
            ATSResult atsResult
    ) {
        Objects.requireNonNull(
                atsResult,
                "atsResult must not be null"
        );

        String prompt = buildPrompt(atsResult);

        RecommendationResponse response =
                aiModelProvider.generate(
                        prompt,
                        RecommendationResponse.class
                );

        if (response == null) {
            throw new IllegalStateException(
                    "AI recommendation service returned null response"
            );
        }

        return response;
    }

    private String buildPrompt(
            ATSResult atsResult
    ) {
        return """
            Analyze the candidate's ATS evaluation and provide
            practical, evidence-based recommendations.

            ATS evaluation:
            %s

            Instructions:

            - Base the assessment only on the ATS evaluation provided.
            - Do not invent candidate skills, experience, education, or
              achievements.
            - Identify genuine strengths from satisfied requirements.
            - Prioritize unmet REQUIRED requirements as critical gaps.
            - Distinguish partial matches from completely unmet requirements.
            - Treat PREFERRED requirements as secondary gaps.
            - Recommendations must be specific and actionable.
            - If a missing requirement may exist in the candidate's
              background but is not demonstrated, phrase it conditionally.
            - Do not recalculate the ATS score.
            - Do not claim that a requirement is satisfied when the ATS
              evaluation says otherwise.
            - Do not claim proficiency, expertise, or experience level unless it is explicitly represented in the ATS evaluation.
            - Treat SATISFIES as requirement satisfaction only; do not reinterpret it as evidence of a particular proficiency level.
            - Describe semantic relationships using the ATS relationship provided.
            - Provide at most 3 strengths.
            - Provide at most 5 critical gaps.
            - Provide at most 5 recommendations.
            - Keep each item concise, preferably one or two sentences.
            - Return only the structured recommendation response.

            """.formatted(atsResult);
    }
}