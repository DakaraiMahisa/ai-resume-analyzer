package com.airesumeanalyzer.backend.processing.service;

import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import com.airesumeanalyzer.backend.common.storage.DocumentStorage;
import com.airesumeanalyzer.backend.jobdescription.entity.JobDescription;
import com.airesumeanalyzer.backend.jobdescription.repository.JobDescriptionRepository;
import com.airesumeanalyzer.backend.processing.exception.ProcessingException;
import com.airesumeanalyzer.backend.processing.extraction.TextExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobDescriptionProcessingService {

    private final JobDescriptionRepository jobDescriptionRepository;
    private final DocumentStorage documentStorage;
    private final TextExtractor textExtractor;

    @Transactional
    public void processJobDescription(UUID jobDescriptionId) {

        JobDescription jobDescription =
                jobDescriptionRepository.findById(jobDescriptionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job description not found."
                                )
                        );

        try (InputStream inputStream =
                     documentStorage.load(
                             jobDescription.getStoragePath()
                     )) {

            String extractedText =
                    textExtractor.extract(inputStream);

            jobDescription.setRawText(extractedText);

        } catch (IOException exception) {
            throw new ProcessingException(
                    "Unable to read the stored job description.",
                    exception
            );
        }
    }
}