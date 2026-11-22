package com.airesumeanalyzer.backend.career.experience.controller;

import com.airesumeanalyzer.backend.auth.security.CurrentUserPrincipal;
import com.airesumeanalyzer.backend.career.experience.dto.request.CreateProfessionalExperienceRequest;
import com.airesumeanalyzer.backend.career.experience.dto.request.UpdateProfessionalExperienceRequest;
import com.airesumeanalyzer.backend.career.experience.dto.response.ProfessionalExperienceResponse;
import com.airesumeanalyzer.backend.career.experience.service.ProfessionalExperienceService;
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
@RequestMapping(ApiRoutes.CAREER_PROFILE + "/experiences")
@RequiredArgsConstructor
public class ProfessionalExperienceController {

    private final ProfessionalExperienceService experienceService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProfessionalExperienceResponse>> createExperience(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @Valid @RequestBody CreateProfessionalExperienceRequest request
    ) {
        ProfessionalExperienceResponse response =
                experienceService.createExperience(principal.getUserId(), request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Professional experience created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProfessionalExperienceResponse>>> getExperiences(
            @AuthenticationPrincipal CurrentUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        experienceService.getExperiences(principal.getUserId())
                )
        );
    }

    @GetMapping("/{experienceId}")
    public ResponseEntity<ApiResponse<ProfessionalExperienceResponse>> getExperience(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID experienceId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        experienceService.getExperience(principal.getUserId(), experienceId)
                )
        );
    }

    @PutMapping("/{experienceId}")
    public ResponseEntity<ApiResponse<ProfessionalExperienceResponse>> updateExperience(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID experienceId,
            @Valid @RequestBody UpdateProfessionalExperienceRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        experienceService.updateExperience(
                                principal.getUserId(),
                                experienceId,
                                request
                        ),
                        "Professional experience updated successfully"
                )
        );
    }

    @DeleteMapping("/{experienceId}")
    public ResponseEntity<ApiResponse<Void>> deleteExperience(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID experienceId
    ) {
        experienceService.deleteExperience(principal.getUserId(), experienceId);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Professional experience deleted successfully")
        );
    }
}
