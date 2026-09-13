package com.airesumeanalyzer.backend.resume.service;

import com.airesumeanalyzer.backend.user.entity.User;
import com.airesumeanalyzer.backend.user.repository.UserRepository;
import com.airesumeanalyzer.backend.common.enums.DocumentProcessingStatus;
import com.airesumeanalyzer.backend.common.exception.base.BadRequestException;
import com.airesumeanalyzer.backend.common.exception.base.ConflictException;
import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import com.airesumeanalyzer.backend.common.exception.base.StorageException;
import com.airesumeanalyzer.backend.common.storage.DocumentStorage;
import com.airesumeanalyzer.backend.common.util.ChecksumUtils;
import com.airesumeanalyzer.backend.processing.entity.ProcessingJob;
import com.airesumeanalyzer.backend.processing.enums.DocumentType;
import com.airesumeanalyzer.backend.processing.service.ProcessingJobService;
import com.airesumeanalyzer.backend.resume.dto.response.ResumeDetailResponse;
import com.airesumeanalyzer.backend.resume.dto.response.ResumeSummaryResponse;
import com.airesumeanalyzer.backend.resume.dto.response.ResumeUploadResponse;
import com.airesumeanalyzer.backend.resume.entity.Resume;
import com.airesumeanalyzer.backend.resume.repository.ResumeRepository;
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
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ProcessingJobService processingJobService;
    private final DocumentStorage documentStorage;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"
    );
    @Transactional
    public ResumeUploadResponse upload(
            UUID userId,
            MultipartFile file
    ) {
        validateFile(file);
        String checksum;

        try {
            checksum = ChecksumUtils.sha256(file.getBytes());
        } catch (IOException exception) {
            throw new StorageException(
                    "Unable to read the uploaded resume. Please try again later.",
                    exception
            );
        }
        Optional<Resume> existingResume =
                resumeRepository.findByChecksum(checksum);

        if (existingResume.isPresent()) {
            throw new ConflictException(
                    "This resume has already been uploaded."
            );
        }
        User owner = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found.")
                );

        Resume resume = Resume.builder()
                .owner(owner)
                .originalFilename(file.getOriginalFilename())
                .checksum(checksum)
                .processingStatus(DocumentProcessingStatus.UPLOADED)
                .build();
        UUID storageKey = UUID.randomUUID();

        String storagePath;

        try {
            storagePath = documentStorage.store(
                    DocumentType.RESUME,
                    storageKey,
                    file.getOriginalFilename(),
                    file.getInputStream()
            );
        } catch (IOException exception) {
            throw new StorageException(
                    "Unable to access the uploaded resume. Please try again later.",
                    exception
            );
        }
        resume.setStoragePath(storagePath);

        Resume savedResume;
        ProcessingJob processingJob;
        try {
            savedResume = resumeRepository.saveAndFlush(resume);

            processingJob = processingJobService.createJob(
                    DocumentType.RESUME,
                    savedResume.getId()
            );
        } catch (DataAccessException exception) {

            try {
                documentStorage.delete(storagePath);
            } catch (StorageException cleanupException) {
                exception.addSuppressed(cleanupException);
            }

            throw exception;
        }

        return toUploadResponse(savedResume,processingJob);
    }

    @Transactional(readOnly = true)
    public List<ResumeSummaryResponse> getResumes(UUID userId) {

        return resumeRepository
                .findAllByOwnerIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResumeDetailResponse getResume(
            UUID resumeId,
            UUID userId
    ) {
        Resume resume = resumeRepository
                .findByIdAndOwnerId(resumeId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resume not found.")
                );

        return toDetailResponse(resume);
    }

    @Transactional
    public void deleteResume(
            UUID resumeId,
            UUID userId
    ) {
        Resume resume = resumeRepository
                .findByIdAndOwnerId(resumeId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resume not found.")
                );

        String storagePath = resume.getStoragePath();

        documentStorage.delete(storagePath);

        resumeRepository.delete(resume);
    }

    private ResumeDetailResponse toDetailResponse(Resume resume) {
        return new ResumeDetailResponse(
                resume.getId(),
                resume.getOriginalFilename(),
                resume.getProcessingStatus(),
                resume.getCreatedAt(),
                resume.getUpdatedAt()
        );
    }

    private ResumeSummaryResponse toSummaryResponse(Resume resume) {
        return new ResumeSummaryResponse(
                resume.getId(),
                resume.getOriginalFilename(),
                resume.getProcessingStatus(),
                resume.getCreatedAt()
        );
    }


    private ResumeUploadResponse toUploadResponse(
            Resume resume,
            ProcessingJob processingJob
    ) {
        return new ResumeUploadResponse(
                resume.getId(),
                processingJob.getId(),
                resume.getProcessingStatus(),
                resume.getCreatedAt()
        );
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
