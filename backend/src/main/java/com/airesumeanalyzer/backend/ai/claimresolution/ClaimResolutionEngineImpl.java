package com.airesumeanalyzer.backend.ai.claimresolution;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.ai.domain.entity.ClaimStatus;
import com.airesumeanalyzer.backend.ai.model.claims.Evidence;
import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import com.airesumeanalyzer.backend.common.entity.Document;
import com.airesumeanalyzer.backend.processing.enums.DocumentType;
import com.airesumeanalyzer.backend.processing.exception.ProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        List<Claim> resolvedClaims = new ArrayList<>();

        for (ProposedClaim claim : claims) {

            Claim resolvedClaim = Claim.builder()
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

            resolvedClaims.add(resolvedClaim);
        }

        return resolvedClaims;
    }

    private String serializeEvidence(List<Evidence> evidence) {
        try {
            return objectMapper.writeValueAsString(evidence);
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