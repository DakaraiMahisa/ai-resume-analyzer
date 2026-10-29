package com.airesumeanalyzer.backend.rie.requirement;

import java.util.List;

public class RequirementClaimConsistencyException
        extends IllegalStateException {

    private final List<String> missingRequirements;

    public RequirementClaimConsistencyException(
            List<String> missingRequirements
    ) {
        super(
                "Job description requirements have no persisted validated "
                        + "claims: "
                        + String.join(", ", missingRequirements)
        );
        this.missingRequirements = List.copyOf(missingRequirements);
    }

    public List<String> getMissingRequirements() {
        return missingRequirements;
    }
}
