package com.airesumeanalyzer.backend.common.api;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        List<String> errors,
        Instant timestamp,
        Pagination pagination
) {

    public static <T> ApiResponse<T> success(T data) {
        return success(data, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(
                true,
                message,
                data,
                null,
                Instant.now(),
                null
        );
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(
                false,
                message,
                null,
                null,
                Instant.now(),
                null
        );
    }

    public static <T> ApiResponse<T> validationError(List<String> errors) {
        return new ApiResponse<>(
                false,
                "Validation failed",
                null,
                errors,
                Instant.now(),
                null
        );
    }
}