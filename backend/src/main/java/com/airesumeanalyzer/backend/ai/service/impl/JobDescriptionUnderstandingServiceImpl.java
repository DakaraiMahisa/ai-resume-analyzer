package com.airesumeanalyzer.backend.ai.service.impl;

import com.airesumeanalyzer.backend.ai.model.understanding.StructuredJobDescription;
import com.airesumeanalyzer.backend.ai.provider.AiModelProvider;
import com.airesumeanalyzer.backend.ai.service.JobDescriptionUnderstandingService;
import com.airesumeanalyzer.backend.common.exception.base.InvalidDocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobDescriptionUnderstandingServiceImpl
        implements JobDescriptionUnderstandingService {

    private final AiModelProvider aiModelProvider;

    @Override
    public StructuredJobDescription understand(String rawText) {

        if (rawText == null || rawText.isBlank()) {
            throw new InvalidDocumentException(
                    "Job description text cannot be empty."
            );
        }

        String prompt = """
        Extract the following job description into the StructuredJobDescription format.

        JOB DESCRIPTION:
        %s

        EXTRACTION RULES:

        1. jobTitle:
           - Return the actual position being hired for.
           - Prefer the title associated with the role being offered.
           - Do not confuse a team name or department name with the job title.

        2. summary:
           - Summarize the role using only information explicitly present in the job description.

        3. responsibilities:
           - Include duties and activities the candidate is expected to perform.
           - Do not include hiring procedures, salary, work timings, or company administrative information.

        4. requiredSkills:
           - Include only skills explicitly identified as required, must-have, or mandatory.
           - Focus on technical skills, programming languages, frameworks, tools, technologies,
             databases, methodologies, and explicitly required technical knowledge.
           - Do not treat soft skills, personality traits, or general candidate characteristics
             as technical skills.

        5. preferredSkills:
           - Include only skills explicitly identified as preferred, good-to-have, or desirable.
           - Do not move required skills into this list.

        6. requiredQualifications:
           - Include explicitly mandatory educational qualifications, degrees, certifications,
             or other formal qualifications.
           - Do not invent qualifications.

        7. preferredQualifications:
           - Include explicitly preferred educational qualifications, certifications,
             or other formal qualifications.

        8. requiredExperience:
           - Include explicitly mandatory experience requirements.
           - Preserve the original meaning and level of experience.

        9. preferredExperience:
           - Include experience explicitly described as preferred, desirable, or giving preference
             to a candidate.
           - Include relevant internships, hackathons, or hands-on experience here only when the
             job description explicitly presents them as preferred experience.

        GENERAL RULES:
        - Do not invent, assume, or infer requirements that are not explicitly present.
        - Preserve the distinction between required and preferred requirements.
        - Do not duplicate the same requirement across multiple fields.
        - Ignore salary, compensation, interview stages, work schedule, and other administrative details
          unless they are directly relevant to the role's responsibilities or requirements.
        - If a category has no applicable information, return an empty list.
        - Return only data matching the StructuredJobDescription schema.
        """.formatted(rawText);

        return aiModelProvider.generate(
                prompt,
                StructuredJobDescription.class
        );

    }
}