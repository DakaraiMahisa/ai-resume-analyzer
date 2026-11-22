package com.airesumeanalyzer.backend.career.preferences.controller;


import com.airesumeanalyzer.backend.career.preferences.dto.request.CreateCandidateCareerPreferenceRequest;
import com.airesumeanalyzer.backend.career.preferences.dto.request.UpdateCandidateCareerPreferenceRequest;
import com.airesumeanalyzer.backend.career.preferences.dto.response.CandidateCareerPreferenceResponse;
import com.airesumeanalyzer.backend.career.preferences.service.CandidateCareerPreferenceService;

import com.airesumeanalyzer.backend.common.api.ApiResponse;
import com.airesumeanalyzer.backend.common.api.ApiRoutes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiRoutes.CAREER_PROFILE + "/preferences")
@RequiredArgsConstructor
public class CandidateCareerPreferenceController {

    private final CandidateCareerPreferenceService preferenceService;

    @PostMapping
    public ResponseEntity<ApiResponse<CandidateCareerPreferenceResponse>> create(
            @Valid @RequestBody CreateCandidateCareerPreferenceRequest request
    ) {
        CandidateCareerPreferenceResponse response =
                preferenceService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        response,
                        "Career preferences created successfully"
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CandidateCareerPreferenceResponse>> getCurrentUserPreferences() {
        CandidateCareerPreferenceResponse response =
                preferenceService.getCurrentUserPreferences();

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Career preferences retrieved successfully"
                )
        );
    }

    @PutMapping
    public ResponseEntity<ApiResponse<CandidateCareerPreferenceResponse>> update(
            @Valid @RequestBody UpdateCandidateCareerPreferenceRequest request
    ) {
        CandidateCareerPreferenceResponse response =
                preferenceService.update(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Career preferences updated successfully"
                )
        );
    }
}
