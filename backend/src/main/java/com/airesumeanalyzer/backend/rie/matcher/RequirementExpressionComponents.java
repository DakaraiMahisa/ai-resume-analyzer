package com.airesumeanalyzer.backend.rie.matcher;

import com.airesumeanalyzer.backend.rie.domain.RequirementComponent;
import com.airesumeanalyzer.backend.rie.domain.RequirementExpression;
import com.airesumeanalyzer.backend.rie.domain.RequirementGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class RequirementExpressionComponents {

    private RequirementExpressionComponents() {
    }

    public static List<RequirementComponent> extract(
            RequirementExpression expression
    ) {
        Objects.requireNonNull(
                expression,
                "expression must not be null"
        );

        List<RequirementComponent> components =
                new ArrayList<>();

        collect(expression, components);

        return List.copyOf(components);
    }

    private static void collect(
            RequirementExpression expression,
            List<RequirementComponent> components
    ) {
        if (expression instanceof RequirementComponent component) {
            components.add(component);
            return;
        }

        if (expression instanceof RequirementGroup group) {
            for (RequirementExpression child :
                    group.expressions()) {

                collect(child, components);
            }

            return;
        }

        throw new IllegalStateException(
                "Unsupported requirement expression: "
                        + expression.getClass().getName()
        );
    }
}