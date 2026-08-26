package com.airesumeanalyzer.backend.common.exception.base;

public class CompromisedCredentialsException extends RuntimeException {
    public CompromisedCredentialsException(String message) {
        super(message);
    }
}

