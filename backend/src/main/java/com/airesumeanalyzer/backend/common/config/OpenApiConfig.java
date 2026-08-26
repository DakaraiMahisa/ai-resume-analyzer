package com.airesumeanalyzer.backend.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(apiInfo())
                .components(securityComponents());
    }

    private Info apiInfo() {

        return new Info()
                .title("AI Resume Analyzer API")
                .version("1.0.0")
                .description("REST API for AI Resume Analyzer")
                .contact(
                        new Contact()
                                .name("AI Resume Analyzer")
                                .email("airesumeanalyzer@gmail.com")
                );
    }

    private Components securityComponents() {

        return new Components()
                .addSecuritySchemes(
                        "bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description(
                                        "JWT Bearer token used to authenticate API requests."
                                )
                );
    }
}