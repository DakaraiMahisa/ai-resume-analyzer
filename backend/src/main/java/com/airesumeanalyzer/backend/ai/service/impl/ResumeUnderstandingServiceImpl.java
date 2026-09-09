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

            GENERAL RULES:
            - Extract only information explicitly present in the resume.
            - Do not invent, infer, assume, or complete missing information.
            - If a scalar field is not explicitly available, return null.
            - If a list field has no available information, return an empty list.
            - Preserve the meaning of the original information.
            - Do not evaluate the candidate.
            - Do not calculate ATS scores.
            - Do not provide recommendations.
            - Do not add information based on common knowledge or assumptions.

            FIELD RULES:

            PersonalInfo:
            - fullName: Extract the candidate's explicitly stated full name.
            - email: Extract the explicitly stated email address.
            - phone: Extract the explicitly stated phone number.
            - location: Extract the explicitly stated location.
            - linkedin: Extract the explicitly stated LinkedIn profile.
            - github: Extract the explicitly stated GitHub profile.

            Summary:
            - Extract the candidate's stated professional summary/objective.
            - Do not create a summary if one is not present.

            Skills:
            - Extract explicitly stated technical or professional skills.
            - Keep each distinct skill as a separate list item where possible.
            - Do not convert responsibilities or achievements into skills unless
              the resume explicitly presents them as skills.
            - Preserve the terminology used in the resume.

            Experience:
            - Extract each explicitly stated work/internship experience.
            - company: Extract the stated organization/company name.
            - jobTitle: Extract the stated position/title.
            - startDate: Extract the stated start date.
            - endDate: Extract the stated end date.
            - description: Extract the responsibilities, activities, and
              accomplishments explicitly associated with that experience.
            - Do not invent dates, titles, companies, or responsibilities.

            Education:
            - Extract each explicitly stated educational qualification.
            - institution: Extract the stated institution name.
            - degree: Extract the stated degree or qualification.
            - fieldOfStudy: Extract the explicitly stated field of study.
            - startDate: Extract the stated start date when available.
            - endDate: Extract the stated end date when available.

            Projects:
            - Extract each explicitly stated project.
            - name: Extract the stated project name.
            - description: Extract the project's stated purpose, functionality,
              or description.
            - technologies: Extract technologies explicitly associated with
              the project.
            - Do not infer technologies merely because they would commonly be
              used for such a project.

            Certifications:
            - Extract explicitly stated certifications.
            - Preserve the certification names as presented in the resume.

            IMPORTANT:
            - Keep resume information separate by section.
            - Do not duplicate information unnecessarily across sections.
            - Do not upgrade vague information into more specific information.
            - Do not interpret implied skills, experience, seniority, or
              qualifications.
            - Return only data conforming to the StructuredResume schema.

            Resume:
            ---
            %s
            ---
            """.formatted(rawText);
    }

}