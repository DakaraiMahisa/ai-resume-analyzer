package com.airesumeanalyzer.backend.auth.security.jwt;


import com.airesumeanalyzer.backend.auth.security.exception.RestAuthenticationEntryPoint;
import com.airesumeanalyzer.backend.user.entity.User;
import com.airesumeanalyzer.backend.user.repository.UserRepository;
import com.airesumeanalyzer.backend.auth.security.CurrentUserPrincipal;
import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String token = extractTokenFromRequest(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (!jwtService.validateToken(token)) {
                SecurityContextHolder.clearContext();
                authenticationEntryPoint.commence(
                        request,
                        response,
                        new BadCredentialsException("Invalid or expired token.")
                );
                return;
            }

            String email = jwtService.extractEmail(token);

            if (email == null) {
                SecurityContextHolder.clearContext();
                authenticationEntryPoint.commence(
                        request,
                        response,
                        new BadCredentialsException("Invalid authentication token.")
                );
                return;
            }

            setSecurityContext(email, request);

        } catch (ResourceNotFoundException exception) {
            SecurityContextHolder.clearContext();

            authenticationEntryPoint.commence(
                    request,
                    response,
                    new BadCredentialsException("Invalid authentication token.")
            );
            return;
        }

        filterChain.doFilter(request, response);
    }


    private String extractTokenFromRequest(HttpServletRequest request) {

        String authorizationHeader =
                request.getHeader(AUTHORIZATION_HEADER);

        if (authorizationHeader != null
                && authorizationHeader.startsWith(BEARER_PREFIX)) {

            return authorizationHeader.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    private void setSecurityContext(
            String email,
            HttpServletRequest request
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found.")
                );

        CurrentUserPrincipal principal =
                new CurrentUserPrincipal(user);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource()
                        .buildDetails(request)
        );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        log.debug("JWT authentication established");
    }
}