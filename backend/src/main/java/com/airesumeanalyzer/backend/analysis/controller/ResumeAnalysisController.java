package com.airesumeanalyzer.backend.analysis.controller;

import com.airesumeanalyzer.backend.analysis.dto.AnalysisDashboardSummaryResponse;
import com.airesumeanalyzer.backend.analysis.dto.AnalysisSummaryResponse;
import com.airesumeanalyzer.backend.analysis.dto.AnalysisStatusResponse;
import com.airesumeanalyzer.backend.auth.security.CurrentUserPrincipal;
import com.airesumeanalyzer.backend.analysis.service.AnalysisQueryService;
import com.airesumeanalyzer.backend.analysis.service.ResumeAnalysisService;

import com.airesumeanalyzer.backend.common.api.ApiResponse;
import com.airesumeanalyzer.backend.common.api.ApiRoutes;
import com.airesumeanalyzer.backend.common.api.Pagination;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiRoutes.ANALYSIS)
@RequiredArgsConstructor
public class ResumeAnalysisController {

    private final ResumeAnalysisService resumeAnalysisService;
    private final AnalysisQueryService analysisQueryService;

    @PostMapping(
            "/resumes/{resumeId}/job-descriptions/{jobDescriptionId}/analyze"
    )
    public ResponseEntity<ApiResponse<AnalysisStatusResponse>> analyze(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID resumeId,
            @PathVariable UUID jobDescriptionId
    ) {

        AnalysisStatusResponse result =
                resumeAnalysisService.start(
                        principal.getUserId(),
                        resumeId,
                        jobDescriptionId
                );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                ApiResponse.success(
                        result,
                        "Resume analysis started"
                )
        );
    }

    @GetMapping("/{analysisId}")
    public ResponseEntity<ApiResponse<AnalysisStatusResponse>> getById(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID analysisId
    ) {

        AnalysisStatusResponse result =
                analysisQueryService.getById(principal.getUserId(), analysisId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        result,
                        "Resume analysis retrieved successfully"
                )
        );
    }

    @PostMapping("/{analysisId}/retry")
    public ResponseEntity<ApiResponse<AnalysisStatusResponse>> retry(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID analysisId
    ) {
        AnalysisStatusResponse result = resumeAnalysisService.retry(
                principal.getUserId(),
                analysisId
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                ApiResponse.success(result, "Analysis retry started")
        );
    }

    @DeleteMapping("/{analysisId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID analysisId
    ) {
        resumeAnalysisService.delete(
                principal.getUserId(),
                analysisId
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AnalysisSummaryResponse>>> getAll(
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        Page<AnalysisSummaryResponse> page =
                analysisQueryService.getAll(pageable);

        Pagination pagination = new Pagination(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        ApiResponse<List<AnalysisSummaryResponse>> response =
                new ApiResponse<>(
                        true,
                        "Resume analyses retrieved successfully",
                        page.getContent(),
                        null,
                        Instant.now(),
                        pagination
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<AnalysisDashboardSummaryResponse>>
    getDashboardSummary() {

        AnalysisDashboardSummaryResponse result =
                analysisQueryService.getDashboardSummary();

        return ResponseEntity.ok(
                ApiResponse.success(
                        result,
                        "Analysis dashboard summary retrieved successfully"
                )
        );
    }
}