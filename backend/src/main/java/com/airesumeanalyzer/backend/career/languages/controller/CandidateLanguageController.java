package com.airesumeanalyzer.backend.career.languages.controller;


import com.airesumeanalyzer.backend.auth.security.CurrentUserPrincipal;
import com.airesumeanalyzer.backend.career.languages.dto.response.CandidateLanguageResponse;
import com.airesumeanalyzer.backend.career.languages.dto.request.CreateCandidateLanguageRequest;
import com.airesumeanalyzer.backend.career.languages.dto.request.UpdateCandidateLanguageRequest;
import com.airesumeanalyzer.backend.career.languages.service.CandidateLanguageService;
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
@RequestMapping(ApiRoutes.CAREER_PROFILE + "/languages")
@RequiredArgsConstructor
public class CandidateLanguageController {

    private final CandidateLanguageService languageService;

    @PostMapping
    public ResponseEntity<ApiResponse<CandidateLanguageResponse>> create(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @Valid @RequestBody CreateCandidateLanguageRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        languageService.create(principal.getUserId(), request)
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CandidateLanguageResponse>>> getAll(
            @AuthenticationPrincipal CurrentUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(languageService.getAll(principal.getUserId()))
        );
    }

    @GetMapping("/{languageId}")
    public ResponseEntity<ApiResponse<CandidateLanguageResponse>> getById(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID languageId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        languageService.getById(principal.getUserId(), languageId)
                )
        );
    }

    @PutMapping("/{languageId}")
    public ResponseEntity<ApiResponse<CandidateLanguageResponse>> update(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID languageId,
            @Valid @RequestBody UpdateCandidateLanguageRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        languageService.update(
                                principal.getUserId(),
                                languageId,
                                request
                        )
                )
        );
    }

    @DeleteMapping("/{languageId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID languageId
    ) {
        languageService.delete(principal.getUserId(), languageId);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Language deleted successfully")
        );
    }
}