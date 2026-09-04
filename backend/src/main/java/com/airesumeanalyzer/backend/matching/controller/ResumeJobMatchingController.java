package com.airesumeanalyzer.backend.matching.controller;

import com.airesumeanalyzer.backend.common.api.ApiResponse;
import com.airesumeanalyzer.backend.common.api.ApiRoutes;
import com.airesumeanalyzer.backend.matching.service.ResumeJobMatchingService;
import com.airesumeanalyzer.backend.rie.domain.RequirementEvaluation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiRoutes.MATCHING)
@RequiredArgsConstructor
public class ResumeJobMatchingController {

    private final ResumeJobMatchingService resumeJobMatchingService;

    @PostMapping(
            "/resumes/{resumeId}/job-descriptions/{jobDescriptionId}/match"
    )
    public ResponseEntity<ApiResponse<List<RequirementEvaluation>>> match(
            @PathVariable UUID resumeId,
            @PathVariable UUID jobDescriptionId
    ) {

        List<RequirementEvaluation> evaluations =
                resumeJobMatchingService.match(
                        resumeId,
                        jobDescriptionId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        evaluations,
                        "Resume matching completed successfully"
                )
        );
    }
}
