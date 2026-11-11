package com.airesumeanalyzer.backend.pii;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Objects;


@Service
public class ResumePiiSanitizer {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
    );


    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "(?<!\\d)(?:\\+?\\d{1,3}[\\s().-]?)?"
                    + "(?:\\(?\\d{2,5}\\)?[\\s().-]?)?"
                    + "\\d{3,5}[\\s.-]?\\d{4}(?!\\d)"
    );

    private static final Pattern URL_PATTERN = Pattern.compile(
            "(?i)(?<![@\\w.-])"
                    + "(?:(?:https?://|www\\.)"
                    + "[a-z0-9](?:[a-z0-9.-]*[a-z0-9])?\\.[a-z]{2,}"
                    + "|(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z]{2,})"
                    + "(?:/[a-z0-9._~:/?#\\[\\]@!$&'()*+,;=%-]*)?"
    );

    private static final Pattern NAME_BOUNDARY_PREFIX = Pattern.compile(
            "[\\p{L}\\p{N}]"
    );

    public SanitizedResumePayload sanitize(
            String rawText,
            String candidateName
    ) {
        Objects.requireNonNull(rawText, "Resume text must not be null");

        PiiRegistry registry = new PiiRegistry();
        String sanitizedText = rawText;

        // 1. Candidate name
        if (candidateName != null && !candidateName.isBlank()) {
            Pattern namePattern = Pattern.compile(
                    "(?i)(?<![\\p{L}\\p{N}])"
                            + Pattern.quote(candidateName.trim())
                            + "(?![\\p{L}\\p{N}])"
            );

            sanitizedText = replaceMatches(
                    sanitizedText,
                    namePattern,
                    PiiType.CANDIDATE_NAME,
                    registry
            );
        }

        // 2. Email addresses
        sanitizedText = replaceMatches(
                sanitizedText,
                EMAIL_PATTERN,
                PiiType.EMAIL,
                registry
        );

        // 3. Phone numbers
        sanitizedText = replaceMatches(
                sanitizedText,
                PHONE_PATTERN,
                PiiType.PHONE,
                registry
        );

        // 4. URLs, including professional and social profiles
        sanitizedText = replaceUrls(sanitizedText, registry);

        // Remove URI prefixes left behind by email/phone replacement.
        sanitizedText = sanitizedText.replaceAll(
                "(?i)\\bmailto:\\s*",
                ""
        );

        sanitizedText = sanitizedText.replaceAll(
                "(?i)\\btel:\\s*",
                ""
        );

        return new SanitizedResumePayload(
                sanitizedText,
                registry.getMappings()
        );
    }

    private String replaceMatches(
            String text,
            Pattern pattern,
            PiiType piiType,
            PiiRegistry registry
    ) {
        Matcher matcher = pattern.matcher(text);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String originalValue = matcher.group();

            String token = registry.register(
                    piiType,
                    originalValue
            );

            matcher.appendReplacement(
                    result,
                    Matcher.quoteReplacement(token)
            );
        }

        matcher.appendTail(result);

        return result.toString();
    }

    private String replaceUrls(
            String text,
            PiiRegistry registry
    ) {
        Matcher matcher = URL_PATTERN.matcher(text);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String matchedUrl = matcher.group();

            // Preserve punctuation immediately following a URL.
            int contentEnd = matchedUrl.length();

            while (contentEnd > 0
                    && ".,;:!?".indexOf(
                    matchedUrl.charAt(contentEnd - 1)
            ) >= 0) {
                contentEnd--;
            }

            if (contentEnd == 0) {
                continue;
            }

            String originalUrl = matchedUrl.substring(0, contentEnd);
            String trailingPunctuation = matchedUrl.substring(contentEnd);

            String token = registry.register(
                    PiiType.URL,
                    originalUrl
            );

            matcher.appendReplacement(
                    result,
                    Matcher.quoteReplacement(token + trailingPunctuation)
            );
        }

        matcher.appendTail(result);

        return result.toString();
    }

    private enum PiiType {
        CANDIDATE_NAME,
        EMAIL,
        PHONE,
        URL
    }

    private static class PiiRegistry {

        private final Map<String, String> mappings = new LinkedHashMap<>();

        private final Map<PiiType, Integer> counters =
                new EnumMap<>(PiiType.class);

        private final Map<String, String> existingTokens =
                new LinkedHashMap<>();

        private String register(PiiType type, String originalValue) {

            String lookupKey = type.name() + ":" + originalValue;

            // Reuse an existing token for repeated values.
            String existingToken = existingTokens.get(lookupKey);

            if (existingToken != null) {
                return existingToken;
            }

            int count = counters.merge(type, 1, Integer::sum);

            String token = switch (type) {
                case CANDIDATE_NAME -> "[CANDIDATE_NAME]";
                case EMAIL -> "[CANDIDATE_EMAIL_" + count + "]";
                case PHONE -> "[CANDIDATE_PHONE_" + count + "]";
                case URL -> "[CANDIDATE_URL_" + count + "]";
            };

            existingTokens.put(lookupKey, token);
            mappings.put(token, originalValue);

            return token;
        }

        private Map<String, String> getMappings() {
            return Collections.unmodifiableMap(
                    new LinkedHashMap<>(mappings)
            );
        }
    }

    public record SanitizedResumePayload(
            String sanitizedText,
            Map<String, String> piiMap
    ) {
        public SanitizedResumePayload {
            Objects.requireNonNull(sanitizedText);
            piiMap = Collections.unmodifiableMap(
                    new LinkedHashMap<>(piiMap)
            );
        }
    }
}
