package com.airesumeanalyzer.backend.processing.service;

import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import com.airesumeanalyzer.backend.processing.exception.ProcessingException;
import com.airesumeanalyzer.backend.processing.extraction.TextExtractor;
import com.airesumeanalyzer.backend.resume.entity.Resume;
import com.airesumeanalyzer.backend.resume.repository.ResumeRepository;
import com.airesumeanalyzer.backend.resume.storage.ResumeStorage;
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
    private final ResumeStorage resumeStorage;
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
                     resumeStorage.load(resume.getStoragePath())) {

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
