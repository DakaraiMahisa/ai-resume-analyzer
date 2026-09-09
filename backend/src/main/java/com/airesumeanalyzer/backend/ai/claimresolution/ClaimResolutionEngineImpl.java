package com.airesumeanalyzer.backend.ai.claimresolution;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.ai.domain.entity.ClaimStatus;
import com.airesumeanalyzer.backend.ai.model.claims.Evidence;
import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import com.airesumeanalyzer.backend.processing.enums.DocumentType;
import com.airesumeanalyzer.backend.processing.exception.ProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ClaimResolutionEngineImpl
        implements ClaimResolutionEngine {

    private final ObjectMapper objectMapper;

    @Override
    public List<Claim> resolve(
            List<ProposedClaim> claims,
            UUID sourceDocumentId,
            DocumentType sourceDocumentType
    ) {

        if (claims == null || claims.isEmpty()) {
            return List.of();
        }

        if (sourceDocumentType == DocumentType.RESUME) {
            return resolveResumeClaims(
                    claims,
                    sourceDocumentId,
                    sourceDocumentType
            );
        }

        return claims.stream()
                .map(claim ->
                        buildClaim(
                                claim,
                                sourceDocumentId,
                                sourceDocumentType
                        )
                )
                .toList();
    }

    private List<Claim> resolveResumeClaims(
            List<ProposedClaim> claims,
            UUID sourceDocumentId,
            DocumentType sourceDocumentType
    ) {

        Map<String, Claim> resolvedClaims = new LinkedHashMap<>();

        for (ProposedClaim claim : claims) {

            String canonicalName =
                    canonicalize(claim.extractedValue());

            String key =
                    claim.claimType().name()
                            + "|"
                            + canonicalName.toLowerCase(Locale.ROOT);

            Claim existingClaim = resolvedClaims.get(key);

            if (existingClaim == null) {

                resolvedClaims.put(
                        key,
                        buildClaim(
                                claim,
                                sourceDocumentId,
                                sourceDocumentType
                        )
                );

            } else {

                existingClaim.setEvidence(
                        mergeEvidence(
                                existingClaim.getEvidence(),
                                claim.evidence()
                        )
                );
            }
        }

        return new ArrayList<>(resolvedClaims.values());
    }

    private Claim buildClaim(
            ProposedClaim claim,
            UUID sourceDocumentId,
            DocumentType sourceDocumentType
    ) {

        return Claim.builder()
                .sourceDocumentId(sourceDocumentId)
                .sourceDocumentType(sourceDocumentType)
                .claimType(claim.claimType())
                .originalValue(claim.extractedValue())
                .canonicalName(
                        canonicalize(claim.extractedValue())
                )
                .priority(claim.priority())
                .status(ClaimStatus.VALIDATED)
                .evidence(
                        serializeEvidence(claim.evidence())
                )
                .build();
    }

    private String mergeEvidence(
            String existingEvidence,
            List<Evidence> newEvidence
    ) {

        try {

            List<Evidence> mergedEvidence =
                    new ArrayList<>(
                            objectMapper.readValue(
                                    existingEvidence,
                                    objectMapper.getTypeFactory()
                                            .constructCollectionType(
                                                    List.class,
                                                    Evidence.class
                                            )
                            )
                    );

            if (newEvidence != null) {
                mergedEvidence.addAll(newEvidence);
            }

            return objectMapper.writeValueAsString(
                    mergedEvidence.stream()
                            .distinct()
                            .toList()
            );

        } catch (Exception exception) {
            throw new ProcessingException(
                    "Unable to merge claim evidence.",
                    exception
            );
        }
    }

    private String serializeEvidence(List<Evidence> evidence) {
        try {
            return objectMapper.writeValueAsString(
                    evidence == null
                            ? List.of()
                            : evidence
            );
        } catch (Exception exception) {
            throw new ProcessingException(
                    "Unable to serialize claim evidence.",
                    exception
            );
        }
    }

    private String canonicalize(String value) {

        if (value == null) {
            return null;
        }

        return value
                .trim()
                .replaceAll("\\s+", " ");
    }
}