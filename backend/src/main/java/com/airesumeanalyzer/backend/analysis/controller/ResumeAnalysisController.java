package com.airesumeanalyzer.backend.analysis.controller;

import com.airesumeanalyzer.backend.analysis.dto.AnalysisDashboardSummaryResponse;
import com.airesumeanalyzer.backend.analysis.dto.AnalysisSummaryResponse;
import com.airesumeanalyzer.backend.analysis.model.ResumeAnalysisResponse;
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
    public ResponseEntity<ApiResponse<ResumeAnalysisResponse>> analyze(
            @PathVariable UUID resumeId,
            @PathVariable UUID jobDescriptionId
    ) {

        ResumeAnalysisResponse result =
                resumeAnalysisService.analyze(
                        resumeId,
                        jobDescriptionId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        result,
                        "Resume analysis completed successfully"
                )
        );
    }

    @GetMapping("/{analysisId}")
    public ResponseEntity<ApiResponse<ResumeAnalysisResponse>> getById(
            @PathVariable UUID analysisId
    ) {

        ResumeAnalysisResponse result =
                analysisQueryService.getById(analysisId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        result,
                        "Resume analysis retrieved successfully"
                )
        );
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