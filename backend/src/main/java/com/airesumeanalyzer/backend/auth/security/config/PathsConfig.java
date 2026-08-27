package com.airesumeanalyzer.backend.auth.security.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PathsConfig {

    @Bean(name = "publicPaths")
    public List<String> publicPaths() {
        return List.of(
                "/error",
                "/api/v1/auth/register/public",
                "/api/v1/auth/login/public",
                "/api/v1/auth/refresh/public",
                "/api/v1/auth/logout/public",
                "/api/swagger-ui.html",
                "/swagger-ui/**",
                "/api/v3/api-docs/**",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/webjars/**"
        );
    }

    @Bean(name = "securedPaths")
    public List<String> securedPaths() {
        return List.of(
                "/api/v1/resumes/**",
                "/api/v1/processing/**",
                "/api/v1/job-descriptions/**"
        );
    }

}
