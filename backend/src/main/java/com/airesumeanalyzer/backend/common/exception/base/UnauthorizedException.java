package com.airesumeanalyzer.backend.common.exception.base;

public class UnauthorizedException extends RuntimeException{
    public UnauthorizedException(String message) {
        super(message);
    }
}
