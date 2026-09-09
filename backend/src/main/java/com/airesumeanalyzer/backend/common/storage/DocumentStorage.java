package com.airesumeanalyzer.backend.common.storage;

import com.airesumeanalyzer.backend.processing.enums.DocumentType;

import java.io.InputStream;
import java.util.UUID;

public interface DocumentStorage {

    String store(
            DocumentType documentType,
            UUID storageKey,
            String originalFilename,
            InputStream content
    );

    InputStream load(String storagePath);

    void delete(String storagePath);
}
