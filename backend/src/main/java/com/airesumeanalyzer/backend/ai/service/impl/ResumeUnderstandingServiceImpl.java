package com.airesumeanalyzer.backend.ai.service.impl;

import com.airesumeanalyzer.backend.ai.model.understanding.StructuredResume;
import com.airesumeanalyzer.backend.ai.provider.AiModelProvider;
import com.airesumeanalyzer.backend.ai.service.ResumeUnderstandingService;
import com.airesumeanalyzer.backend.common.exception.base.InvalidDocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResumeUnderstandingServiceImpl
        implements ResumeUnderstandingService {

    private final AiModelProvider aiModelProvider;

    @Override
    public StructuredResume understand(String rawText) {

        if (rawText == null || rawText.isBlank()) {
            throw new InvalidDocumentException(
                    "Resume text cannot be empty."
            );
        }

        String prompt = buildPrompt(rawText);

        return aiModelProvider.generate(
                prompt,
                StructuredResume.class
        );
    }

    private String buildPrompt(String rawText) {

        return """
                You are a resume information extraction system.

                Extract structured information from the resume text
                provided below.

                Rules:
                - Extract only information explicitly present in the resume.
                - Do not invent or infer information.
                - If information is missing, return null.
                - For list fields, return an empty list when no information
                  is available.
                - Preserve the meaning of the original information.
                - Do not evaluate the candidate.
                - Do not calculate ATS scores.
                - Do not provide recommendations.

                Resume:
                ---
                %s
                ---
                """.formatted(rawText);
    }
}