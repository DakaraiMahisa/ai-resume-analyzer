package com.airesumeanalyzer.backend.pii;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ResumePiiSanitizerTest {

    private ResumePiiSanitizer sanitizer;

    @BeforeEach
    void setUp() {
        sanitizer = new ResumePiiSanitizer();
    }

    private static final String RAW_RESUME = """
            
            Marcus Vance, M.S.E.E.
            Senior Hardware Electrical Engineer — Power Electronics

            Email: marcus.vance@example.com Phone: (214) 555-0182 Location: Dallas, TX

            LinkedIn: linkedin.com/in/marcus-vance-eee

            PROFESSIONAL SUMMARY

            Accomplished Senior Electrical Engineer with over 6 years of hands-on experience in board-level hardware design,
            high-voltage DC-DC power converters, and switch-mode power supply (SMPS) architectures.

            TECHNICAL EXPERTISE

            Power Topologies: Buck, Boost, LLC Resonant, Flyback, Synchronous Rectification, GaN/SiC Power Stages

            PROFESSIONAL EXPERIENCE

            Senior Hardware Power Engineer — Navitas Semiconductor Jan 2022 – Present

            Dallas, TX

            Architected high-density 800V DC-DC power converter modules utilizing Gallium Nitride (GaN) IC switches,
            achieving 98.4% peak efficiency and 30% power density increase.

            EDUCATION

            Master of Science in Electrical Engineering (MSEE) — University of Texas at Austin May 2018

            Specialization: Power Electronics & Solid-State Devices | GPA: 3.92/4.0
            """;

    @Test
    void shouldMaskCandidateName() {
        var result = sanitizer.sanitize(RAW_RESUME, "Marcus Vance");

        assertTrue(result.sanitizedText().contains("[CANDIDATE_NAME]"));
        assertFalse(result.sanitizedText().contains("Marcus Vance"));

        assertEquals(
                "Marcus Vance",
                result.piiMap().get("[CANDIDATE_NAME]")
        );
    }

    @Test
    void shouldMaskEmailAddress() {
        var result = sanitizer.sanitize(RAW_RESUME, "Marcus Vance");

        assertFalse(result.sanitizedText().contains("marcus.vance@example.com"));
        assertTrue(result.sanitizedText().contains("[CANDIDATE_EMAIL_1]"));

        assertEquals(
                "marcus.vance@example.com",
                result.piiMap().get("[CANDIDATE_EMAIL_1]")
        );
    }

    @Test
    void shouldMaskPhoneNumber() {
        var result = sanitizer.sanitize(RAW_RESUME, "Marcus Vance");

        assertFalse(result.sanitizedText().contains("(214) 555-0182"));
        assertTrue(result.sanitizedText().contains("[CANDIDATE_PHONE_1]"));

        assertEquals(
                "(214) 555-0182",
                result.piiMap().get("[CANDIDATE_PHONE_1]")
        );
    }

    @Test
    void shouldMaskLinkedInUrl() {
        var result = sanitizer.sanitize(RAW_RESUME, "Marcus Vance");

        assertFalse(
                result.sanitizedText().contains(
                        "linkedin.com/in/marcus-vance-eee"
                )
        );

        assertTrue(result.sanitizedText().contains("[CANDIDATE_URL_1]"));

        assertEquals(
                "linkedin.com/in/marcus-vance-eee",
                result.piiMap().get("[CANDIDATE_URL_1]")
        );
    }

    @Test
    void shouldPreserveNonPiiResumeContent() {
        var result = sanitizer.sanitize(RAW_RESUME, "Marcus Vance");

        assertTrue(result.sanitizedText().contains(
                "Senior Hardware Electrical Engineer"
        ));

        assertTrue(result.sanitizedText().contains(
                "Power Topologies: Buck, Boost, LLC Resonant"
        ));

        assertTrue(result.sanitizedText().contains(
                "98.4% peak efficiency"
        ));

        assertTrue(result.sanitizedText().contains(
                "GPA: 3.92/4.0"
        ));

        assertTrue(result.sanitizedText().contains(
                "Navitas Semiconductor"
        ));
    }

    @Test
    void shouldReuseTokensForRepeatedPiiValues() {
        String rawText = """
                Marcus Vance
                Email: marcus.vance@example.com
                Alternative email: marcus.vance@example.com
                Phone: (214) 555-0182
                Another phone reference: (214) 555-0182
                LinkedIn: linkedin.com/in/marcus-vance-eee
                Profile: linkedin.com/in/marcus-vance-eee
                """;

        var result = sanitizer.sanitize(rawText, "Marcus Vance");

        assertEquals(
                2,
                countOccurrences(
                        result.sanitizedText(),
                        "[CANDIDATE_EMAIL_1]"
                )
        );

        assertEquals(
                2,
                countOccurrences(
                        result.sanitizedText(),
                        "[CANDIDATE_PHONE_1]"
                )
        );

        assertEquals(
                2,
                countOccurrences(
                        result.sanitizedText(),
                        "[CANDIDATE_URL_1]"
                )
        );

        assertEquals(4, result.piiMap().size());
    }

    @Test
    void shouldAssignDifferentTokensToDifferentPiiValues() {
        String rawText = """
                Marcus Vance
                marcus.vance@example.com
                marcus.personal@example.com
                (214) 555-0182
                (214) 555-0199
                linkedin.com/in/marcus-vance-eee
                github.com/marcusvance
                """;

        var result = sanitizer.sanitize(rawText, "Marcus Vance");

        Map<String, String> piiMap = result.piiMap();

        assertEquals(
                "marcus.vance@example.com",
                piiMap.get("[CANDIDATE_EMAIL_1]")
        );

        assertEquals(
                "marcus.personal@example.com",
                piiMap.get("[CANDIDATE_EMAIL_2]")
        );

        assertEquals(
                "(214) 555-0182",
                piiMap.get("[CANDIDATE_PHONE_1]")
        );

        assertEquals(
                "(214) 555-0199",
                piiMap.get("[CANDIDATE_PHONE_2]")
        );

        assertEquals(
                "linkedin.com/in/marcus-vance-eee",
                piiMap.get("[CANDIDATE_URL_1]")
        );

        assertEquals(
                "github.com/marcusvance",
                piiMap.get("[CANDIDATE_URL_2]")
        );
    }

    @Test
    void shouldHandleNullCandidateName() {
        String rawText = "Email: marcus.vance@example.com";

        var result = sanitizer.sanitize(rawText, null);

        assertTrue(result.sanitizedText().contains("[CANDIDATE_EMAIL_1]"));
        assertFalse(result.sanitizedText().contains("marcus.vance@example.com"));
        assertFalse(result.piiMap().containsKey("[CANDIDATE_NAME]"));
    }

    @Test
    void shouldRejectNullResumeText() {
        assertThrows(
                NullPointerException.class,
                () -> sanitizer.sanitize(null, "Marcus Vance")
        );
    }

    @Test
    void shouldReturnEmptyPiiMapWhenNoPiiIsPresent() {
        String rawText = """
                Senior Hardware Electrical Engineer
                Power Electronics
                Experience with GaN and SiC power devices.
                """;

        var result = sanitizer.sanitize(rawText, null);

        assertEquals(rawText, result.sanitizedText());
        assertTrue(result.piiMap().isEmpty());
    }

    @Test
    void shouldReturnImmutablePiiMap() {
        var result = sanitizer.sanitize(
                "Marcus Vance",
                "Marcus Vance"
        );

        assertThrows(
                UnsupportedOperationException.class,
                () -> result.piiMap().put(
                        "[EXTRA]",
                        "Unexpected value"
                )
        );
    }

    private int countOccurrences(String text, String token) {
        return (text.length() - text.replace(token, "").length())
                / token.length();
    }

}
