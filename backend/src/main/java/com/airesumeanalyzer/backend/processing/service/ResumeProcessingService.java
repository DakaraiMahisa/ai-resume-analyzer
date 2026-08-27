package com.airesumeanalyzer.backend.processing.service;

import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import com.airesumeanalyzer.backend.common.storage.DocumentStorage;
import com.airesumeanalyzer.backend.processing.exception.ProcessingException;
import com.airesumeanalyzer.backend.processing.extraction.TextExtractor;
import com.airesumeanalyzer.backend.resume.entity.Resume;
import com.airesumeanalyzer.backend.resume.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResumeProcessingService {

    private final ResumeRepository resumeRepository;
    private final DocumentStorage documentStorage;
    private final TextExtractor textExtractor;


    @Transactional
    public void processResume(UUID resumeId) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resume not found."
                        )
                );

        try (InputStream inputStream =
                     documentStorage.load(resume.getStoragePath())) {

            String extractedText =
                    textExtractor.extract(inputStream);

            resume.setRawText(extractedText);

        } catch (IOException exception) {
            throw new ProcessingException(
                    "Unable to read the stored resume.",
                    exception
            );
        }
    }
}
