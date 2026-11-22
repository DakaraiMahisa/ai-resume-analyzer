package com.airesumeanalyzer.backend.career.achievements.controller;


import com.airesumeanalyzer.backend.auth.security.CurrentUserPrincipal;
import com.airesumeanalyzer.backend.career.achievements.dto.response.CandidateAchievementResponse;
import com.airesumeanalyzer.backend.career.achievements.dto.request.CreateCandidateAchievementRequest;
import com.airesumeanalyzer.backend.career.achievements.dto.request.UpdateCandidateAchievementRequest;
import com.airesumeanalyzer.backend.career.achievements.service.CandidateAchievementService;
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
@RequestMapping(ApiRoutes.CAREER_PROFILE + "/achievements")
@RequiredArgsConstructor
public class CandidateAchievementController {

    private final CandidateAchievementService achievementService;

    @PostMapping
    public ResponseEntity<ApiResponse<CandidateAchievementResponse>> create(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @Valid @RequestBody CreateCandidateAchievementRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        achievementService.create(principal.getUserId(), request)
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CandidateAchievementResponse>>> getAll(
            @AuthenticationPrincipal CurrentUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(achievementService.getAll(principal.getUserId()))
        );
    }

    @GetMapping("/{achievementId}")
    public ResponseEntity<ApiResponse<CandidateAchievementResponse>> getById(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID achievementId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        achievementService.getById(principal.getUserId(), achievementId)
                )
        );
    }

    @PutMapping("/{achievementId}")
    public ResponseEntity<ApiResponse<CandidateAchievementResponse>> update(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID achievementId,
            @Valid @RequestBody UpdateCandidateAchievementRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        achievementService.update(
                                principal.getUserId(),
                                achievementId,
                                request
                        )
                )
        );
    }

    @DeleteMapping("/{achievementId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID achievementId
    ) {
        achievementService.delete(principal.getUserId(), achievementId);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Achievement deleted successfully")
        );
    }
}
