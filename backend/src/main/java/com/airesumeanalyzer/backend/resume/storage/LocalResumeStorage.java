package com.airesumeanalyzer.backend.resume.storage;

import com.airesumeanalyzer.backend.common.exception.base.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class LocalResumeStorage implements ResumeStorage {

    private final Path storageRoot;

    public LocalResumeStorage(
            @Value("${app.storage.resume-directory:./storage/resumes}")
            String storageDirectory
    ) {
        this.storageRoot = Paths.get(storageDirectory)
                .toAbsolutePath()
                .normalize();
    }

    @Override
    public String store(
            UUID storageKey,
            String originalFilename,
            InputStream content
    ) {
        String extension = extractExtension(originalFilename);

        Path resumeDirectory = storageRoot.resolve(storageKey.toString());
        Path targetPath = resumeDirectory.resolve(
                "resume" + extension
        );

        try {
            Files.createDirectories(resumeDirectory);

            Files.copy(
                    content,
                    targetPath
            );

            return storageRoot.relativize(targetPath)
                    .toString()
                    .replace('\\', '/');

        } catch (IOException exception) {
            throw new StorageException(
                    "Failed to store resume: " + storageKey,
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
                    "Invalid resume storage path"
            );
        }

        try {
            return Files.newInputStream(targetPath);
        } catch (IOException exception) {
            throw new StorageException(
                    "Failed to read resume: " + storagePath,
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
                    "Invalid resume storage path"
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
                    "Failed to delete resume: " + storagePath,
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
