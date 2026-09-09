package com.airesumeanalyzer.backend.jobdescription.controller;

import com.airesumeanalyzer.backend.auth.security.CurrentUserPrincipal;
import com.airesumeanalyzer.backend.common.api.ApiResponse;
import com.airesumeanalyzer.backend.common.api.ApiRoutes;
import com.airesumeanalyzer.backend.jobdescription.dto.request.JobDescriptionTextRequest;
import com.airesumeanalyzer.backend.jobdescription.dto.response.JobDescriptionDetailResponse;
import com.airesumeanalyzer.backend.jobdescription.dto.response.JobDescriptionSummaryResponse;
import com.airesumeanalyzer.backend.jobdescription.dto.response.JobDescriptionUploadResponse;
import com.airesumeanalyzer.backend.jobdescription.service.JobDescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiRoutes.JOB_DESCRIPTIONS)
@RequiredArgsConstructor
public class JobDescriptionController {

    private final JobDescriptionService jobDescriptionService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<JobDescriptionUploadResponse>> upload(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @RequestParam("file") MultipartFile file
    ) {
        JobDescriptionUploadResponse response =
                jobDescriptionService.upload(
                        principal.getUserId(),
                        file
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                response,
                                "Job description uploaded successfully"
                        )
                );
    }

    @PostMapping("/text")
    public ResponseEntity<ApiResponse<JobDescriptionUploadResponse>> createFromText(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @RequestBody JobDescriptionTextRequest request
    ) {
        JobDescriptionUploadResponse response =
                jobDescriptionService.createFromText(
                        principal.getUserId(),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                response,
                                "Job description created successfully"
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobDescriptionSummaryResponse>>> getAll(
            @AuthenticationPrincipal CurrentUserPrincipal principal
    ) {
        List<JobDescriptionSummaryResponse> response =
                jobDescriptionService.getAll(
                        principal.getUserId()
                );

        return ResponseEntity
                .ok(
                        ApiResponse.success(
                                response,
                                "Job descriptions retrieved successfully"
                        )
                );
    }

    @GetMapping("/{jobDescriptionId}")
    public ResponseEntity<ApiResponse<JobDescriptionDetailResponse>> get(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID jobDescriptionId
    ) {
        JobDescriptionDetailResponse response =
                jobDescriptionService.get(
                        jobDescriptionId,
                        principal.getUserId()
                );

        return ResponseEntity
                .ok(
                        ApiResponse.success(
                                response,
                                "Job description retrieved successfully"
                        )
                );
    }

    @DeleteMapping("/{jobDescriptionId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID jobDescriptionId
    ) {
        jobDescriptionService.delete(
                jobDescriptionId,
                principal.getUserId()
        );

        return ResponseEntity
                .ok(
                        ApiResponse.success(
                                null,
                                "Job description deleted successfully"
                        )
                );
    }
}