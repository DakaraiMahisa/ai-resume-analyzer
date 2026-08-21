package com.airesumeanalyzer.backend.common.exception.base;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}