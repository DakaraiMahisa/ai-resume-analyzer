package com.airesumeanalyzer.backend.career.projects.controller;

import com.airesumeanalyzer.backend.auth.security.CurrentUserPrincipal;
import com.airesumeanalyzer.backend.career.projects.dto.request.CreateCandidateProjectRequest;
import com.airesumeanalyzer.backend.career.projects.dto.request.UpdateCandidateProjectRequest;
import com.airesumeanalyzer.backend.career.projects.dto.response.CandidateProjectResponse;
import com.airesumeanalyzer.backend.career.projects.service.CandidateProjectService;
import com.airesumeanalyzer.backend.common.api.ApiResponse;
import com.airesumeanalyzer.backend.common.api.ApiRoutes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiRoutes.CAREER_PROFILE + "/projects")
@RequiredArgsConstructor
public class CandidateProjectController {

    private final CandidateProjectService candidateProjectService;

    @PostMapping
    public ResponseEntity<ApiResponse<CandidateProjectResponse>> createProject(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @Valid @RequestBody CreateCandidateProjectRequest request
    ) {
        CandidateProjectResponse response =
                candidateProjectService.createProject(
                        principal.getUserId(), request
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        response, "Project created successfully"
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CandidateProjectResponse>>> getProjects(
            @AuthenticationPrincipal CurrentUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        candidateProjectService.getProjects(
                                principal.getUserId()
                        ),
                        "Projects fetched successfully"
                )
        );
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<CandidateProjectResponse>> getProject(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID projectId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        candidateProjectService.getProject(
                                principal.getUserId(), projectId
                        ),
                        "Project fetched successfully"
                )
        );
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<CandidateProjectResponse>> updateProject(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID projectId,
            @Valid @RequestBody UpdateCandidateProjectRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        candidateProjectService.updateProject(
                                principal.getUserId(), projectId, request
                        ),
                        "Project updated successfully"
                )
        );
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID projectId
    ) {
        candidateProjectService.deleteProject(
                principal.getUserId(), projectId
        );

        return ResponseEntity.ok(
                ApiResponse.success(null, "Project deleted successfully")
        );
    }
}
