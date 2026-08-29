package com.airesumeanalyzer.backend.ai.evidence;

import com.airesumeanalyzer.backend.ai.evidence.impl.DeterministicEvidenceLocator;
import com.airesumeanalyzer.backend.ai.model.claims.Evidence;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeterministicEvidenceLocatorTest {

    private final EvidenceLocator evidenceLocator =
            new DeterministicEvidenceLocator();

    @Test
    void shouldLocateAllExactOccurrences() {

        String rawText =
                "Experienced with Spring Boot. "
                        + "Built Spring Boot applications.";

        List<Evidence> evidence =
                evidenceLocator.locate(
                        rawText,
                        "Spring Boot"
                );

        assertEquals(2, evidence.size());

        assertEquals(
                "Spring Boot",
                evidence.get(0).text()
        );

        assertEquals(
                "Spring Boot",
                evidence.get(1).text()
        );

        assertEquals(
                "Spring Boot",
                rawText.substring(
                        evidence.get(0).startOffset(),
                        evidence.get(0).endOffset()
                )
        );

        assertEquals(
                "Spring Boot",
                rawText.substring(
                        evidence.get(1).startOffset(),
                        evidence.get(1).endOffset()
                )
        );
    }

    @Test
    void shouldReturnEmptyListWhenEvidenceIsNotFound() {

        String rawText =
                "Experienced Java developer with MySQL.";

        List<Evidence> evidence =
                evidenceLocator.locate(
                        rawText,
                        "Spring Boot"
                );

        assertTrue(evidence.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenRawTextIsBlank() {

        List<Evidence> evidence =
                evidenceLocator.locate(
                        "   ",
                        "Spring Boot"
                );

        assertTrue(evidence.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenExtractedValueIsBlank() {

        String rawText =
                "Experienced Java developer.";

        List<Evidence> evidence =
                evidenceLocator.locate(
                        rawText,
                        "   "
                );

        assertTrue(evidence.isEmpty());
    }

    @Test
    void shouldLocateEvidenceCaseInsensitively() {

        String rawText =
                "Java developer. "
                        + "JAVA programmer. "
                        + "java backend engineer.";

        List<Evidence> evidence =
                evidenceLocator.locate(
                        rawText,
                        "java"
                );

        assertEquals(3, evidence.size());

        assertEquals("Java", evidence.get(0).text());
        assertEquals("JAVA", evidence.get(1).text());
        assertEquals("java", evidence.get(2).text());

        evidence.forEach(item ->
                assertEquals(
                        item.text(),
                        rawText.substring(
                                item.startOffset(),
                                item.endOffset()
                        )
                )
        );
    }

}