package com.airesumeanalyzer.backend.pii;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CandidateNameResolverTest {

    private CandidateNameResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new CandidateNameResolver();
    }

    @Test
    void shouldExtractNamesFromResumeHeaders() {

        String[][] testCases = {
                {"Brandon Miller", "Brandon Miller"},
                {"John H. Doe", "John H. Doe"},
                {"Jane O'Connor", "Jane O'Connor"},
                {"Mary-Jane Smith", "Mary-Jane Smith"},
                {"PROFESSIONAL SUMMARY", null},
                {"john@example.com", null},
                {"JOHN DOE", null}
        };

        for (String[] testCase : testCases) {

            String input = testCase[0];
            String expected = testCase[1];

            String actual = resolver.extractFromHeader(input);


            assertEquals(expected, actual,
                    "Unexpected result for input: " + input);
        }
    }

    @Test
    void shouldExtractNamesFromFilenames() {

        String[][] testCases = {
                {"Brandon_Miller_Resume.pdf", "Brandon Miller"},
                {"Jane-OConnor-CV.docx", "Jane OConnor"},
                {"John_Doe_Final.doc", "John Doe"},
                {"Resume_Final.pdf", null},
                {"random-document.pdf", null},
                {"", null},
                {null, null}
        };

        for (String[] testCase : testCases) {

            String input = testCase[0];
            String expected = testCase[1];

            String actual = resolver.extractFromFilename(input);


            assertEquals(expected, actual,
                    "Unexpected result for filename: " + input);
        }
    }

    @Test
    void shouldPrioritiseResumeHeaderOverFilename() {

        String actual = resolver.resolveCandidateName(
                "Jane_Smith_Resume.pdf",
                "John Doe\njohn@example.com\nSoftware Engineer"
        );


        assertEquals("John Doe", actual);
    }

    @Test
    void shouldFallbackToFilenameWhenHeaderHasNoName() {

        String actual = resolver.resolveCandidateName(
                "Jane_Smith_Resume.pdf",
                "PROFESSIONAL SUMMARY\nExperienced Java Developer"
        );


        assertEquals("Jane Smith", actual);
    }

    @Test
    void shouldReturnNullWhenNoNameCanBeResolved() {

        String actual = resolver.resolveCandidateName(
                "Resume_Final.pdf",
                "PROFESSIONAL SUMMARY\nExperienced Java Developer"
        );

        assertEquals(null, actual);
    }


    @Test
    void shouldResolveCandidateNameFromRealisticResumeText() {

        String rawText = """
            
            Brandon Miller
            Financial & General Ledger Analyst

            Email: brandon.miller.fin@example.com Phone: (917) 555-0143 Location: Brooklyn, NY

            LinkedIn: linkedin.com/in/brandon-miller-fin

            PROFESSIONAL SUMMARY

            Detail-oriented Financial Analyst with 3.5 years of experience in corporate accounting support,
            operational budget tracking, and month-end General Ledger reconciliations.

            SKILLS & SOFTWARE

            Financial Systems: SAP ERP (FI/CO), QuickBooks Enterprise, Microsoft Access

            PROFESSIONAL EXPERIENCE

            Corporate Financial Analyst — Apex Logistics Solutions Sep 2023 – Jul 2025

            EDUCATION

            Bachelor of Science in General Business Administration
            """;

        String actual = resolver.resolveCandidateName(
                "Brandon_Miller_Resume.pdf",
                rawText
        );


        assertEquals("Brandon Miller", actual);
    }

    @Test
    void shouldResolveCandidateNameFromRealisticStudentResume() {
        String rawText = """

            Happy Taera
            Electronics & Communication
            Engineering Student | Aspiring
            Backend Developer | Java | Spring Boot

            happytaera@gmail.com
            8224764602
            KPR Institute of Engineering and
            Technology,Coimbatore, Tamil nadu

            Skills
            Java
            Intermediate
            """;

        String result = resolver.resolveCandidateName(
                "resume.pdf",
                rawText
        );

        assertEquals("Happy Taera", result);
    }

}
