package com.airesumeanalyzer.backend.career.profile.controller;

import com.airesumeanalyzer.backend.auth.security.CurrentUserPrincipal;
import com.airesumeanalyzer.backend.career.profile.dto.request.CreateCandidateProfileRequest;
import com.airesumeanalyzer.backend.career.profile.dto.request.UpdateCandidateProfileRequest;
import com.airesumeanalyzer.backend.career.profile.dto.response.CandidateProfileCompletionResponse;
import com.airesumeanalyzer.backend.career.profile.dto.response.CandidateProfileResponse;
import com.airesumeanalyzer.backend.career.profile.service.CandidateProfileService;
import com.airesumeanalyzer.backend.common.api.ApiResponse;
import com.airesumeanalyzer.backend.common.api.ApiRoutes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiRoutes.CAREER_PROFILE)
@RequiredArgsConstructor
public class CandidateProfileController {

    private final CandidateProfileService candidateProfileService;

    @PostMapping
    public ResponseEntity<ApiResponse<CandidateProfileResponse>> createProfile(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @Valid @RequestBody CreateCandidateProfileRequest request
    ) {
        CandidateProfileResponse response =
                candidateProfileService.createProfile(
                        principal.getUserId(),
                        request
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        response,
                        "Candidate profile created successfully"
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CandidateProfileResponse>> getProfile(
            @AuthenticationPrincipal CurrentUserPrincipal principal
    ) {
        CandidateProfileResponse response =
                candidateProfileService.getProfile(principal.getUserId());

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Candidate profile retrieved successfully"
                )
        );
    }

    @PutMapping
    public ResponseEntity<ApiResponse<CandidateProfileResponse>> updateProfile(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @Valid @RequestBody UpdateCandidateProfileRequest request
    ) {
        CandidateProfileResponse response =
                candidateProfileService.updateProfile(
                        principal.getUserId(),
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Candidate profile updated successfully"
                )
        );
    }

    @GetMapping("/completion")
    public ResponseEntity<ApiResponse<CandidateProfileCompletionResponse>> getProfileCompletion(
            @AuthenticationPrincipal CurrentUserPrincipal principal
    ) {
        CandidateProfileCompletionResponse response =
                candidateProfileService.getProfileCompletion(principal.getUserId());

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Candidate profile completion retrieved successfully"
                )
        );
    }
}