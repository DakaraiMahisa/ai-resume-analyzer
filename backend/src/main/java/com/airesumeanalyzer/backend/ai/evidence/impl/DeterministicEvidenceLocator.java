package com.airesumeanalyzer.backend.ai.evidence.impl;

import com.airesumeanalyzer.backend.ai.evidence.EvidenceLocator;
import com.airesumeanalyzer.backend.ai.model.claims.Evidence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@Slf4j
public class DeterministicEvidenceLocator implements EvidenceLocator {

    @Override
    public List<Evidence> locate(
            String rawText,
            String extractedValue
    ) {

        List<Evidence> evidenceList = new ArrayList<>();

        if (rawText == null || rawText.isBlank()) {
            return evidenceList;
        }

        if (extractedValue == null || extractedValue.isBlank()) {
            return evidenceList;
        }

        String searchValue = extractedValue.trim();

        String searchableText = rawText.toLowerCase(Locale.ROOT);
        String searchableValue = searchValue.toLowerCase(Locale.ROOT);

        int fromIndex = 0;

        while (fromIndex < searchableText.length()) {

            int startOffset =
                    searchableText.indexOf(
                            searchableValue,
                            fromIndex
                    );

            if (startOffset == -1) {
                break;
            }

            int endOffset =
                    startOffset + searchableValue.length();

            if (isValidBoundary(
                    searchableText,
                    startOffset,
                    endOffset
            )) {

                evidenceList.add(
                        Evidence.builder()
                                .text(
                                        rawText.substring(
                                                startOffset,
                                                endOffset
                                        )
                                )
                                .startOffset(startOffset)
                                .endOffset(endOffset)
                                .build()
                );
            }

            fromIndex = endOffset;
        }

        return evidenceList;
    }

    private boolean isValidBoundary(
            String text,
            int startOffset,
            int endOffset
    ) {

        boolean validLeftBoundary =
                startOffset == 0
                        || !isTokenCharacter(
                        text.charAt(startOffset - 1)
                );

        boolean validRightBoundary =
                endOffset == text.length()
                        || !isTokenCharacter(
                        text.charAt(endOffset)
                );

        return validLeftBoundary && validRightBoundary;
    }

    private boolean isTokenCharacter(char character) {

        return Character.isLetterOrDigit(character);
    }
}