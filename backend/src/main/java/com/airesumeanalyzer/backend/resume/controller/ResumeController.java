package com.airesumeanalyzer.backend.resume.controller;

import com.airesumeanalyzer.backend.auth.security.CurrentUserPrincipal;
import com.airesumeanalyzer.backend.common.api.ApiResponse;
import com.airesumeanalyzer.backend.common.api.ApiRoutes;
import com.airesumeanalyzer.backend.resume.dto.response.ResumeDetailResponse;
import com.airesumeanalyzer.backend.resume.dto.response.ResumeSummaryResponse;
import com.airesumeanalyzer.backend.resume.dto.response.ResumeUploadResponse;
import com.airesumeanalyzer.backend.resume.service.ResumeService;
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
@RequestMapping(ApiRoutes.RESUMES)
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ResumeUploadResponse>> upload(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @RequestParam("file") MultipartFile file
    ) {
        ResumeUploadResponse response = resumeService.upload(
                principal.getUserId(),
                file
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                response,
                                "Resume uploaded successfully"
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ResumeSummaryResponse>>> getResumes(
            @AuthenticationPrincipal CurrentUserPrincipal principal
    ) {
        List<ResumeSummaryResponse> response =
                resumeService.getResumes(principal.getUserId());

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Resumes retrieved successfully"
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResumeDetailResponse>> getResume(
            @PathVariable UUID id,
            @AuthenticationPrincipal CurrentUserPrincipal principal
    ) {
        ResumeDetailResponse response = resumeService.getResume(
                id,
                principal.getUserId()
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Resume retrieved successfully"
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteResume(
            @PathVariable UUID id,
            @AuthenticationPrincipal CurrentUserPrincipal principal
    ) {
        resumeService.deleteResume(
                id,
                principal.getUserId()
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Resume deleted successfully"
                )
        );
    }

}
