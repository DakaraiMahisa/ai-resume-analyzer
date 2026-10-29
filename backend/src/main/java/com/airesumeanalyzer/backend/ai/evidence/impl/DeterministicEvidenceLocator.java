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

        NormalizedText normalizedText = normalize(rawText);
        String searchableText = normalizedText.text();
        String searchableValue = normalizeWhitespace(extractedValue)
                .toLowerCase(Locale.ROOT);

        log.info(
                "Evidence exact-match diagnostic. searchValue={}, rawTextContains={}, rawTextLength={}",
                searchValue,
                searchableText.contains(searchableValue),
                rawText.length()
        );
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

                int originalStart =
                        normalizedText.originalStarts().get(startOffset);

                int originalEnd =
                        normalizedText.originalEnds().get(endOffset - 1);

                evidenceList.add(
                        Evidence.builder()
                                .text(rawText.substring(originalStart, originalEnd))
                                .startOffset(originalStart)
                                .endOffset(originalEnd)
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

    private NormalizedText normalize(String originalText) {
        StringBuilder normalized = new StringBuilder();
        List<Integer> originalStarts = new ArrayList<>();
        List<Integer> originalEnds = new ArrayList<>();

        int i = 0;

        while (i < originalText.length()) {
            if (Character.isWhitespace(originalText.charAt(i))) {
                int start = i;

                while (i < originalText.length()
                        && Character.isWhitespace(originalText.charAt(i))) {
                    i++;
                }

                if (!normalized.isEmpty()) {
                    normalized.append(' ');
                    originalStarts.add(start);
                    originalEnds.add(i);
                }
            } else {
                normalized.append(
                        Character.toLowerCase(originalText.charAt(i)));
                originalStarts.add(i);
                originalEnds.add(i + 1);
                i++;
            }
        }

        if (!normalized.isEmpty()
                && normalized.charAt(normalized.length() - 1) == ' ') {
            int last = normalized.length() - 1;
            normalized.deleteCharAt(last);
            originalStarts.remove(last);
            originalEnds.remove(last);
        }

        return new NormalizedText(
                normalized.toString(), originalStarts, originalEnds);
    }

    private String normalizeWhitespace(String value) {
        return value.trim().replaceAll("\\s+", " ");
    }

    private record NormalizedText(
            String text,
            List<Integer> originalStarts,
            List<Integer> originalEnds) {
    }
}