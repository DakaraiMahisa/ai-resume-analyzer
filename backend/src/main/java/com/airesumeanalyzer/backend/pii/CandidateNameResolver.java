package com.airesumeanalyzer.backend.pii;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CandidateNameResolver {

    private static final Pattern NAME_HEADER_PATTERN = Pattern.compile(
            "^[A-Z][a-z]*(?:[A-Z][a-z]+|['-][A-Z]?[a-z]+)*" +
                    "(?:\\s+(?:[A-Z]\\.|[A-Z][a-z]*(?:[A-Z][a-z]+|['-][A-Z]?[a-z]+)*)){1,3}$"
    );


    private static final Set<String> EXCLUDED_HEADERS = Set.of(
            "PROFESSIONAL SUMMARY",
            "WORK EXPERIENCE",
            "EXECUTIVE SUMMARY",
            "CAREER OBJECTIVE",
            "TECHNICAL SKILLS",
            "EDUCATION BACKGROUND"
    );

    public String resolveCandidateName(String originalFilename, String rawText) {
        // Priority 1: Document Text Header (Most accurate for candidate identity)
        String headerName = extractFromHeader(rawText);
        if (headerName != null) {
            return headerName;
        }

        // Priority 2: Original Filename Fallback
        String filenameName = extractFromFilename(originalFilename);
        if (filenameName != null) {
            return filenameName;
        }

        return null;
    }

    public String extractFromHeader(String rawText) {
        if (rawText == null || rawText.isBlank()) return null;

        String[] lines = rawText.split("\\r?\\n");
        for (int i = 0; i < Math.min(lines.length, 10); i++) {
            String line = lines[i].trim();

            if (line.isEmpty()
                    || line.equalsIgnoreCase("resume")
                    || line.equalsIgnoreCase("curriculum vitae")
                    || line.equalsIgnoreCase("cv")
                    || line.contains("@")
                    || line.contains("http")
                    || line.contains("www.")
                    || line.matches(".*\\d{3}.*")) {
                continue;
            }

            String sanitizedLine = line.replaceAll(
                    "(?i),\\s*(pmp|cpa|phd|md|bsc|msc|ba|bs)\\b", ""
            ).trim();


            if (isSectionHeader(sanitizedLine)) {
                break;
            }

            Matcher matcher = NAME_HEADER_PATTERN.matcher(sanitizedLine);
            if (matcher.matches()) {
                return sanitizedLine;
            }
        }
        return null;
    }

    public String extractFromFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) return null;

        String clean = originalFilename
                .replaceAll("(?i)\\.(pdf|docx|doc)$", "")
                .replaceAll("(?i)[_-]+", " ")
                .replaceAll("(?i)\\b(resume|cv|updated|final|draft)\\b", " ")
                .replaceAll("\\s+", " ")
                .trim();

        Matcher matcher = NAME_HEADER_PATTERN.matcher(clean);
        return matcher.matches() ? clean : null;
    }

    private boolean isSectionHeader(String line) {
        return EXCLUDED_HEADERS.contains(line.trim().toUpperCase());
    }
}