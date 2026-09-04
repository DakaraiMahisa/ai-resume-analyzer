package com.airesumeanalyzer.backend.rie.domain;

public sealed interface RequirementExpression
        permits RequirementComponent, RequirementGroup {
}