package com.airesumeanalyzer.backend.rie.requirement;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.rie.domain.RequirementComponent;
import com.airesumeanalyzer.backend.rie.domain.RequirementExpression;
import com.airesumeanalyzer.backend.rie.domain.RequirementGroup;
import com.airesumeanalyzer.backend.rie.domain.RequirementOperator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class SimpleRequirementDecomposer
        implements RequirementDecomposer {

    @Override
    public RequirementExpression decompose(
            Claim requirement
    ) {

        if (requirement == null) {
            throw new IllegalArgumentException(
                    "requirement must not be null"
            );
        }

        String value = requirement.getCanonicalName();

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "requirement canonical name must not be blank"
            );
        }

        String normalizedValue = value.trim();

        return parseParenthesizedGroups(
                requirement.getId(),
                normalizedValue
        );
    }


    private RequirementExpression parseExpression(
            UUID requirementClaimId,
            String expression
    ) {

        List<String> parts =
                Arrays.stream(expression.split(","))
                        .map(String::trim)
                        .filter(part -> !part.isBlank())
                        .toList();

        if (parts.size() > 1) {

            List<RequirementExpression> expressions =
                    parts.stream()
                            .map(part ->
                                    parseAlternativeExpression(
                                            requirementClaimId,
                                            part
                                    )
                            )
                            .toList();

            return new RequirementGroup(
                    requirementClaimId,
                    RequirementOperator.ALL,
                    expressions
            );
        }

        return parseAlternativeExpression(
                requirementClaimId,
                expression
        );
    }

    private RequirementExpression parseAlternativeExpression(
            UUID requirementClaimId,
            String expression
    ) {

        List<String> alternatives =
                Arrays.stream(expression.split("/"))
                        .map(String::trim)
                        .filter(part -> !part.isBlank())
                        .toList();

        if (alternatives.size() > 1) {

            List<RequirementExpression> expressions =
                    alternatives.stream()
                            .map(part ->
                                    new RequirementComponent(
                                            requirementClaimId,
                                            part
                                    )
                            )
                            .map(expressionPart ->
                                    (RequirementExpression) expressionPart
                            )
                            .toList();

            return new RequirementGroup(
                    requirementClaimId,
                    RequirementOperator.ANY,
                    expressions
            );
        }

        return new RequirementComponent(
                requirementClaimId,
                expression
        );
    }

    private RequirementExpression parseParenthesizedGroups(
            UUID requirementClaimId,
            String expression
    ) {

        List<RequirementExpression> expressions =
                new ArrayList<>();

        StringBuilder textOutsideGroups =
                new StringBuilder();

        int depth = 0;
        int groupStart = -1;

        for (int i = 0; i < expression.length(); i++) {

            char current = expression.charAt(i);

            if (current == '(') {

                if (depth == 0) {
                    groupStart = i;
                }

                depth++;

                continue;
            }

            if (current == ')') {

                if (depth == 0) {
                    throw new IllegalArgumentException(
                            "Unmatched closing parenthesis"
                    );
                }

                depth--;

                if (depth == 0) {

                    String groupContent =
                            expression.substring(
                                    groupStart + 1,
                                    i
                            ).trim();

                    String groupPrefix =
                            textOutsideGroups
                                    .toString()
                                    .trim();

                    textOutsideGroups.setLength(0);

                    RequirementExpression groupExpression =
                            parseExpression(
                                    requirementClaimId,
                                    groupContent
                            );

                    if (!groupPrefix.isBlank()) {

                        groupPrefix =
                                removeLeadingConjunction(
                                        groupPrefix
                                );

                        if (!groupPrefix.isBlank()) {

                            RequirementExpression prefixExpression =
                                    parseExpression(
                                            requirementClaimId,
                                            groupPrefix
                                    );

                            groupExpression =
                                    new RequirementGroup(
                                            requirementClaimId,
                                            RequirementOperator.ALL,
                                            List.of(
                                                    prefixExpression,
                                                    groupExpression
                                            )
                                    );
                        }
                    }

                    expressions.add(groupExpression);

                    groupStart = -1;
                }

                continue;
            }

            if (depth == 0) {
                textOutsideGroups.append(current);
            }
        }

        if (depth != 0) {
            throw new IllegalArgumentException(
                    "Unmatched opening parenthesis"
            );
        }

        String remainingText =
                textOutsideGroups
                        .toString()
                        .trim();

        if (!remainingText.isBlank()) {
            expressions.add(
                    parseExpression(
                            requirementClaimId,
                            remainingText
                    )
            );
        }

        if (expressions.isEmpty()) {
            return new RequirementComponent(
                    requirementClaimId,
                    expression
            );
        }

        if (expressions.size() == 1) {
            return expressions.get(0);
        }

        return new RequirementGroup(
                requirementClaimId,
                RequirementOperator.ALL,
                expressions
        );
    }

    private String removeLeadingConjunction(
            String value
    ) {

        String normalizedValue =
                value.trim();

        if (normalizedValue.regionMatches(
                true,
                0,
                "and ",
                0,
                4
        )) {
            return normalizedValue
                    .substring(4)
                    .trim();
        }

        if (normalizedValue.startsWith("&")) {
            return normalizedValue
                    .substring(1)
                    .trim();
        }

        return normalizedValue;
    }
}