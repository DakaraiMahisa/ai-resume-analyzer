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
        Extract the following job description into the
        StructuredJobDescription schema.

        JOB DESCRIPTION:
        %s

        ============================================================
        EXTRACTION RULES
        ============================================================

        1. JOB TITLE
        - Extract the job title exactly as stated in the source.
        - Preserve the role level and designation, such as Intern,
          Junior, Senior, Lead, Engineer, Manager, etc.
        - Do NOT infer, upgrade, downgrade, normalize, or substitute
          the title based on responsibilities, skills, or seniority.
        - Do NOT replace an "Intern" role with "Engineer" merely because
          the responsibilities involve engineering work.
        - If the source says "AI/ML Intern", jobTitle must be
          "AI/ML Intern".
        - Do not invent a job title.

        2. SUMMARY
        - Summarize the role using only information present in the
          source.
        - Do not introduce requirements that are not stated.

        3. RESPONSIBILITIES
        - Extract duties and activities the candidate is expected to
          perform.
        - Keep each responsibility as a complete activity.
        - Do not convert responsibilities into skills.

        4. SKILLS
        requiredSkills and preferredSkills must contain technical
        capabilities only.

        Include technologies such as:
        programming languages, frameworks, libraries, databases,
        cloud platforms, developer tools, ML/AI technologies, and
        explicitly required technical knowledge.

        Do NOT classify soft skills or general characteristics such as
        communication, teamwork, passion, motivation, creativity, or
        being a quick learner as technical skills.

        Preserve required versus preferred classification.

        5. QUALIFICATIONS
        requiredQualifications and preferredQualifications contain
        formal qualifications such as degrees, certifications,
        licenses, or other explicitly stated qualifications.

        Do not classify ordinary technical skills as qualifications.

        6. EXPERIENCE
        requiredExperience and preferredExperience contain explicitly
        stated experience requirements.

        Preserve important context such as duration, domain,
        technology, seniority, internships, hackathons, and hands-on
        experience when the source explicitly presents them as
        experience.

        ============================================================
        REQUIREMENT STRUCTURE
        ============================================================

        Each requirement has:

        - value
        - operator
        - components
        - originalText

        operator must be one of:
        ATOMIC, ALL, ANY

        ATOMIC:
        Use for one indivisible matchable requirement.

        Example:
        "Python"

        {
          "value": "Python",
          "operator": "ATOMIC",
          "components": [],
          "originalText": "Python"
        }

        ALL:
        Use when multiple requirements must be satisfied together.

        A parent value may be retained when the parent itself is also
        a meaningful matchable capability.

        Example:
        "Python (pandas, numpy, data structures)"

        {
          "value": "Python",
          "operator": "ALL",
          "components": [
            {"value": "pandas", "operator": "ATOMIC"},
            {"value": "numpy", "operator": "ATOMIC"},
            {"value": "data structures", "operator": "ATOMIC"}
          ]
        }

        ANY:
        Use when alternatives are explicitly presented and one
        alternative can satisfy the requirement.

        Structural grouping labels are NOT requirements themselves.

        Example:
        "at least one ML framework: scikit-learn / PyTorch / TensorFlow"

        {
          "value": null,
          "operator": "ANY",
          "components": [
            {"value": "scikit-learn", "operator": "ATOMIC"},
            {"value": "PyTorch", "operator": "ATOMIC"},
            {"value": "TensorFlow", "operator": "ATOMIC"}
          ]
        }

        "ML framework" is only a category, so it must not become a
        separate claim.

        Nested relationships must be preserved.

        Example:
        "LangChain/LlamaIndex AND FAISS/Chroma/Pinecone"

        should represent:

        ALL(
            ANY(LangChain, LlamaIndex),
            ANY(FAISS, Chroma, Pinecone)
        )

        Structural grouping nodes should have value = null.

        A node should have a non-null value only when the node itself
        represents something a candidate can meaningfully possess or
        demonstrate.

        Do NOT decide this solely from the operator. An ALL or ANY node
        may be either matchable or structural.

        Do not blindly interpret punctuation:
        - "/" does not always mean ANY.
        - "," does not always mean ALL.
        - Established terms such as "CI/CD" must remain atomic.

        Determine relationships from the meaning of the source text.

        ============================================================
        ORIGINAL TEXT
        ============================================================

        Preserve the source wording that caused each requirement to
        be extracted.

        Do not rewrite or normalize originalText.

        ============================================================
        QUALITY RULES
        ============================================================

        - Extract only information supported by the source.
        - Do not invent requirements, technologies, qualifications,
          experience, or job titles.
        - Preserve required versus preferred classification.
        - Do not duplicate requirements across categories.
        - Preserve logical ALL/ANY relationships.
        - Prefer atomic matchable capabilities where appropriate.
        - Do not split established technical terms unnecessarily.
        - Correct obvious OCR/extraction errors only when the intended
          meaning is unambiguous.
        - Use empty lists when a category has no applicable information.

        ============================================================
        OUTPUT
        ============================================================

        Return only valid data matching the StructuredJobDescription
        schema.

        For every Requirement:
        - value is the matchable requirement, or null for a structural
          grouping node.
        - operator is ATOMIC, ALL, or ANY.
        - components contains child requirements when applicable.
        - ATOMIC requirements have no components.
        - originalText preserves the source wording.

        Do not include explanatory text outside the structured output.
        """.formatted(rawText);

        return aiModelProvider.generate(
                prompt,
                StructuredJobDescription.class
        );
    }
}

