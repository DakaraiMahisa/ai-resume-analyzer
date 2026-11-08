
package com.airesumeanalyzer.backend.common.storage;

import com.airesumeanalyzer.backend.common.exception.base.StorageException;
import com.airesumeanalyzer.backend.processing.enums.DocumentType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.Locale;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "app.storage.provider",
        havingValue = "b2"
)
public class B2DocumentStorage implements DocumentStorage {

    private final S3Client s3Client;
    private final B2StorageProperties properties;

    @Override
    public String store(
            DocumentType documentType,
            UUID storageKey,
            String originalFilename,
            InputStream content,
            long contentLength
    ) {
        if (content == null) {
            throw new StorageException("Document content cannot be null.");
        }

        if (contentLength < 0) {
            throw new StorageException("Document content length cannot be negative.");
        }

        String objectKey = generateObjectKey(
                documentType,
                storageKey,
                originalFilename
        );

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(objectKey)
                    .contentLength(contentLength)
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(content, contentLength)
            );

            return objectKey;

        } catch (SdkException exception) {
            throw new StorageException(
                    "Failed to store document in Backblaze B2.",
                    exception
            );
        }
    }

    @Override
    public InputStream load(String storagePath) {
        validateStoragePath(storagePath);

        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(storagePath)
                    .build();

            return s3Client.getObject(request);

        } catch (SdkException exception) {
            throw new StorageException(
                    "Failed to load document from Backblaze B2.",
                    exception
            );
        }
    }

    @Override
    public void delete(String storagePath) {
        validateStoragePath(storagePath);

        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(storagePath)
                    .build();

            s3Client.deleteObject(request);

        } catch (SdkException exception) {
            throw new StorageException(
                    "Failed to delete document from Backblaze B2.",
                    exception
            );
        }
    }

    private String generateObjectKey(
            DocumentType documentType,
            UUID storageKey,
            String originalFilename
    ) {
        if (documentType == null || storageKey == null) {
            throw new StorageException(
                    "Document type and storage key are required."
            );
        }

        String extension = extractExtension(originalFilename);

        return switch (documentType) {
            case RESUME ->
                    "resumes/" + storageKey + "/resume" + extension;

            case JOB_DESCRIPTION ->
                    "job-descriptions/" + storageKey
                            + "/job-description" + extension;
        };
    }

    private String extractExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return "";
        }

        int lastDot = filename.lastIndexOf('.');

        if (lastDot < 0) {
            return "";
        }

        return filename.substring(lastDot).toLowerCase(Locale.ROOT);
    }

    private void validateStoragePath(String storagePath) {
        if (storagePath == null || storagePath.isBlank()
                || storagePath.startsWith("/")
                || storagePath.contains("\\")
                || storagePath.contains("..")) {
            throw new StorageException("Invalid document storage path.");
        }

        if (!storagePath.startsWith("resumes/")
                && !storagePath.startsWith("job-descriptions/")) {
            throw new StorageException("Unsupported document storage path.");
        }
    }
}
