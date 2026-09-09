package com.airesumeanalyzer.backend.rie.domain;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record RequirementGroup(
        RequirementOperator operator,
        List<RequirementExpression> expressions
) implements RequirementExpression {

    public RequirementGroup {
        Objects.requireNonNull(
                operator,
                "operator must not be null"
        );

        Objects.requireNonNull(
                expressions,
                "expressions must not be null"
        );

        if (expressions.isEmpty()) {
            throw new IllegalArgumentException(
                    "expressions must not be empty"
            );
        }

        if (expressions.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "expressions must not contain null elements"
            );
        }

        expressions = List.copyOf(expressions);
    }
}