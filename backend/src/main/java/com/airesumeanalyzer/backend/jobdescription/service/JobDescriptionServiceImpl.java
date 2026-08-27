package com.airesumeanalyzer.backend.jobdescription.service;

import com.airesumeanalyzer.backend.auth.entity.User;
import com.airesumeanalyzer.backend.auth.repository.UserRepository;
import com.airesumeanalyzer.backend.common.enums.DocumentProcessingStatus;
import com.airesumeanalyzer.backend.common.exception.base.BadRequestException;
import com.airesumeanalyzer.backend.common.exception.base.ConflictException;
import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import com.airesumeanalyzer.backend.common.exception.base.StorageException;
import com.airesumeanalyzer.backend.common.storage.DocumentStorage;
import com.airesumeanalyzer.backend.common.util.ChecksumUtils;
import com.airesumeanalyzer.backend.jobdescription.dto.request.JobDescriptionTextRequest;
import com.airesumeanalyzer.backend.jobdescription.dto.response.JobDescriptionDetailResponse;
import com.airesumeanalyzer.backend.jobdescription.dto.response.JobDescriptionSummaryResponse;
import com.airesumeanalyzer.backend.jobdescription.dto.response.JobDescriptionUploadResponse;
import com.airesumeanalyzer.backend.jobdescription.entity.JobDescription;
import com.airesumeanalyzer.backend.jobdescription.repository.JobDescriptionRepository;
import com.airesumeanalyzer.backend.processing.enums.DocumentType;
import com.airesumeanalyzer.backend.processing.service.ProcessingJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobDescriptionServiceImpl implements JobDescriptionService {

    private final JobDescriptionRepository jobDescriptionRepository;
    private final UserRepository userRepository;
    private final DocumentStorage documentStorage;
    private final ProcessingJobService processingJobService;


    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"
    );


    @Override
    @Transactional
    public JobDescriptionUploadResponse upload(
            UUID userId,
            MultipartFile file
    ) {
        validateFile(file);

        String checksum;

        try {
            checksum = ChecksumUtils.sha256(file.getBytes());
        } catch (IOException exception) {
            throw new StorageException(
                    "Unable to read the uploaded job description. Please try again later.",
                    exception
            );
        }

        Optional<JobDescription> existingJobDescription =
                jobDescriptionRepository.findByChecksum(checksum);

        if (existingJobDescription.isPresent()) {
            throw new ConflictException(
                    "This job description has already been uploaded."
            );
        }

        User owner = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found.")
                );

        JobDescription jobDescription = JobDescription.builder()
                .owner(owner)
                .originalFilename(file.getOriginalFilename())
                .checksum(checksum)
                .processingStatus(DocumentProcessingStatus.UPLOADED)
                .build();

        UUID storageKey = UUID.randomUUID();

        String storagePath;

        try {
            storagePath = documentStorage.store(
                    DocumentType.JOB_DESCRIPTION,
                    storageKey,
                    file.getOriginalFilename(),
                    file.getInputStream()
            );
        } catch (IOException exception) {
            throw new StorageException(
                    "Unable to access the uploaded job description. Please try again later.",
                    exception
            );
        }

        jobDescription.setStoragePath(storagePath);

        JobDescription savedJobDescription;

        try {
            savedJobDescription =
                    jobDescriptionRepository.saveAndFlush(jobDescription);

            processingJobService.createJob(
                    DocumentType.JOB_DESCRIPTION,
                    savedJobDescription.getId()
            );

        } catch (DataAccessException exception) {

            try {
                documentStorage.delete(storagePath);
            } catch (StorageException cleanupException) {
                exception.addSuppressed(cleanupException);
            }

            throw exception;
        }

        return new JobDescriptionUploadResponse(
                savedJobDescription.getId(),
                savedJobDescription.getProcessingStatus(),
                savedJobDescription.getCreatedAt()
        );
    }


    @Override
    @Transactional(readOnly = true)
    public JobDescriptionDetailResponse get(
            UUID jobDescriptionId,
            UUID userId
    ) {
        JobDescription jobDescription =
                jobDescriptionRepository
                        .findByIdAndOwnerId(jobDescriptionId, userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job description not found."
                                )
                        );

        return new JobDescriptionDetailResponse(
                jobDescription.getId(),
                jobDescription.getOriginalFilename(),
                jobDescription.getProcessingStatus(),
                jobDescription.getCreatedAt(),
                jobDescription.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public JobDescriptionUploadResponse createFromText(
            UUID userId,
            JobDescriptionTextRequest request
    ) {
        if (request.content() == null || request.content().isBlank()) {
            throw new BadRequestException(
                    "Job description content must not be empty."
            );
        }

        User owner = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found.")
                );

        JobDescription jobDescription = JobDescription.builder()
                .owner(owner)
                .originalFilename(null)
                .storagePath(null)
                .rawText(request.content())
                .processingStatus(DocumentProcessingStatus.COMPLETED)
                .build();

        JobDescription savedJobDescription =
                jobDescriptionRepository.saveAndFlush(jobDescription);

        return new JobDescriptionUploadResponse(
                savedJobDescription.getId(),
                savedJobDescription.getProcessingStatus(),
                savedJobDescription.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobDescriptionSummaryResponse> getAll(UUID userId) {

        return jobDescriptionRepository
                .findAllByOwnerId(userId)
                .stream()
                .map(jobDescription ->
                        new JobDescriptionSummaryResponse(
                                jobDescription.getId(),
                                jobDescription.getOriginalFilename(),
                                jobDescription.getProcessingStatus(),
                                jobDescription.getCreatedAt()
                        )
                )
                .toList();
    }



    @Override
    @Transactional
    public void delete(
            UUID jobDescriptionId,
            UUID userId
    ) {
        JobDescription jobDescription =
                jobDescriptionRepository
                        .findByIdAndOwnerId(jobDescriptionId, userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job description not found."
                                )
                        );

        String storagePath = jobDescription.getStoragePath();

        documentStorage.delete(storagePath);

        jobDescriptionRepository.delete(jobDescription);
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Resume file must not be empty.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException(
                    "Resume file must not exceed 5 MB."
            );
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BadRequestException(
                    "Unsupported resume file type. Only PDF, DOCX, and TXT files are allowed."
            );
        }
    }
}
