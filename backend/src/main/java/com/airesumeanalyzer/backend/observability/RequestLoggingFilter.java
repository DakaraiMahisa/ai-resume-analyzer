package com.airesumeanalyzer.backend.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long startTime = System.nanoTime();
        int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

        try {
            filterChain.doFilter(request, response);
            status = response.getStatus();

        } catch (IOException | ServletException | RuntimeException ex) {
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            throw ex;

        } finally {
            long durationMs = TimeUnit.NANOSECONDS.toMillis(
                    System.nanoTime() - startTime
            );

            logRequest(request, status, durationMs);
        }
    }

    private void logRequest(
            HttpServletRequest request,
            int status,
            long durationMs
    ) {
        LoggingEventBuilder event;

        if (status >= 500) {
            event = log.atError();
        } else if (status >= 400) {
            event = log.atWarn();
        } else {
            event = log.atInfo();
        }

        event
                .addKeyValue("event", "http_request")
                .addKeyValue("method", request.getMethod())
                .addKeyValue("uri", request.getRequestURI())
                .addKeyValue("status", status)
                .addKeyValue("durationMs", durationMs)
                .log("HTTP request completed");
    }
}