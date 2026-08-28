package com.airesumeanalyzer.backend.common.exception.base;

public class InvalidDocumentException extends RuntimeException {

    public InvalidDocumentException(String message) {
        super(message);
    }
}