package com.airesumeanalyzer.backend.common.storage;

import com.airesumeanalyzer.backend.common.exception.base.StorageException;
import com.airesumeanalyzer.backend.processing.enums.DocumentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class LocalDocumentStorage implements DocumentStorage {

    private final Path storageRoot;

    public LocalDocumentStorage(
            @Value("${app.storage.document-directory:./storage/documents}")
            String storageDirectory
    ) {
        this.storageRoot = Paths.get(storageDirectory)
                .toAbsolutePath()
                .normalize();
    }

    @Override
    public String store(
            DocumentType documentType,
            UUID storageKey,
            String originalFilename,
            InputStream content
    ) {
        String extension = extractExtension(originalFilename);

        String directoryName = switch (documentType) {
            case RESUME -> "resumes";
            case JOB_DESCRIPTION -> "job-descriptions";
        };

        String fileName = switch (documentType) {
            case RESUME -> "resume";
            case JOB_DESCRIPTION -> "job-description";
        };

        Path documentDirectory = storageRoot
                .resolve(directoryName)
                .resolve(storageKey.toString());

        Path targetPath = documentDirectory.resolve(
                fileName + extension
        );

        try {
            Files.createDirectories(documentDirectory);

            Files.copy(
                    content,
                    targetPath
            );

            return storageRoot.relativize(targetPath)
                    .toString()
                    .replace('\\', '/');

        } catch (IOException exception) {
            throw new StorageException(
                    "Failed to store document: " + storageKey,
                    exception
            );
        }
    }

    @Override
    public InputStream load(String storagePath) {
        Path targetPath = storageRoot
                .resolve(storagePath)
                .normalize();

        if (!targetPath.startsWith(storageRoot)) {
            throw new StorageException(
                    "Invalid document storage path"
            );
        }

        try {
            return Files.newInputStream(targetPath);
        } catch (IOException exception) {
            throw new StorageException(
                    "Failed to read document: " + storagePath,
                    exception
            );
        }
    }

    @Override
    public void delete(String storagePath) {

        Path targetPath = storageRoot
                .resolve(storagePath)
                .normalize();

        if (!targetPath.startsWith(storageRoot)) {
            throw new StorageException(
                    "Invalid document storage path"
            );
        }

        try {
            Files.deleteIfExists(targetPath);

            Path parentDirectory = targetPath.getParent();

            if (parentDirectory != null
                    && parentDirectory.startsWith(storageRoot)) {
                Files.deleteIfExists(parentDirectory);
            }

        } catch (IOException exception) {
            throw new StorageException(
                    "Failed to delete document: " + storagePath,
                    exception
            );
        }
    }

    private String extractExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return "";
        }

        int extensionIndex = filename.lastIndexOf('.');

        if (extensionIndex < 0) {
            return "";
        }

        return filename.substring(extensionIndex).toLowerCase();
    }
}

