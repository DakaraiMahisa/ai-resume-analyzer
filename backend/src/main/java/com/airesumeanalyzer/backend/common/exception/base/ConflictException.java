package com.airesumeanalyzer.backend.common.exception.base;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}