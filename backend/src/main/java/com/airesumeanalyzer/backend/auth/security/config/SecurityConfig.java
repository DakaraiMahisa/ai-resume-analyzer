package com.airesumeanalyzer.backend.auth.security.config;

import com.airesumeanalyzer.backend.auth.security.exception.RestAccessDeniedHandler;
import com.airesumeanalyzer.backend.auth.security.exception.RestAuthenticationEntryPoint;
import com.airesumeanalyzer.backend.auth.security.jwt.JwtAuthenticationFilter;
import com.airesumeanalyzer.backend.observability.RequestCorrelationFilter;
import com.airesumeanalyzer.backend.observability.RequestLoggingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;


import java.util.List;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Qualifier("publicPaths")
    private final List<String> publicPaths;

    @Qualifier("securedPaths")
    private final List<String> securedPaths;

    @Qualifier("adminPaths")
    private final List<String> adminPaths;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RequestCorrelationFilter requestCorrelationFilter;
    private final RequestLoggingFilter requestLoggingFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(requests -> {

                    adminPaths.forEach(path ->
                            requests.requestMatchers(path).hasRole("ADMIN")
                    );
                    publicPaths.forEach(path ->
                            requests.requestMatchers(path).permitAll()
                    );

                    securedPaths.forEach(path ->
                            requests.requestMatchers(path).authenticated()
                    );
                    requests.anyRequest().denyAll();
                })
                .addFilterBefore(
                        requestLoggingFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(
                        requestCorrelationFilter,
                        JwtAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationProvider authenticationProvider
    ) {
        return new ProviderManager(authenticationProvider);
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }

    @Bean
    public FilterRegistrationBean<RequestCorrelationFilter>
    requestCorrelationFilterRegistration(
            RequestCorrelationFilter filter
    ) {
        FilterRegistrationBean<RequestCorrelationFilter> registration =
                new FilterRegistrationBean<>(filter);

        registration.setEnabled(false);

        return registration;
    }

    @Bean
    public FilterRegistrationBean<RequestLoggingFilter>
    requestLoggingFilterRegistration(
            RequestLoggingFilter filter
    ) {
        FilterRegistrationBean<RequestLoggingFilter> registration =
                new FilterRegistrationBean<>(filter);

        registration.setEnabled(false);

        return registration;
    }
}