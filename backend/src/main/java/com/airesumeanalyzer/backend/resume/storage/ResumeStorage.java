package com.airesumeanalyzer.backend.resume.storage;

import java.io.InputStream;
import java.util.UUID;

public interface ResumeStorage {

    String store(
            UUID storageKey,
            String originalFilename,
            InputStream content
    );
    InputStream load(String storagePath);
    void delete(String storagePath);
}
