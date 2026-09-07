package com.airesumeanalyzer.backend.analysis.controller;

import com.airesumeanalyzer.backend.analysis.model.ResumeAnalysisResponse;
import com.airesumeanalyzer.backend.analysis.service.ResumeAnalysisService;

import com.airesumeanalyzer.backend.common.api.ApiResponse;
import com.airesumeanalyzer.backend.common.api.ApiRoutes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiRoutes.ANALYSIS)
@RequiredArgsConstructor
public class ResumeAnalysisController {

    private final ResumeAnalysisService resumeAnalysisService;

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
}