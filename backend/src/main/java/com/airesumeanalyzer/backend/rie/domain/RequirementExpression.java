package com.airesumeanalyzer.backend.rie.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(
                value = RequirementComponent.class,
                name = "component"
        ),
        @JsonSubTypes.Type(
                value = RequirementGroup.class,
                name = "group"
        )
})
public sealed interface RequirementExpression
        permits RequirementComponent, RequirementGroup {
}