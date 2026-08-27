package com.airesumeanalyzer.backend.jobdescription.service;

import com.airesumeanalyzer.backend.jobdescription.dto.request.JobDescriptionTextRequest;
import com.airesumeanalyzer.backend.jobdescription.dto.response.JobDescriptionDetailResponse;
import com.airesumeanalyzer.backend.jobdescription.dto.response.JobDescriptionSummaryResponse;
import com.airesumeanalyzer.backend.jobdescription.dto.response.JobDescriptionUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface JobDescriptionService {

    JobDescriptionUploadResponse upload(
            UUID userId,
            MultipartFile file
    );

    JobDescriptionDetailResponse get(
            UUID jobDescriptionId,
            UUID userId
    );

    List<JobDescriptionSummaryResponse> getAll(
            UUID userId
    );

    JobDescriptionUploadResponse createFromText(
            UUID userId,
            JobDescriptionTextRequest request
    );

    void delete(
            UUID jobDescriptionId,
            UUID userId
    );
}