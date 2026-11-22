package com.airesumeanalyzer.backend.career.education.controller;

import com.airesumeanalyzer.backend.auth.security.CurrentUserPrincipal;
import com.airesumeanalyzer.backend.career.education.dto.request.CreateEducationRequest;
import com.airesumeanalyzer.backend.career.education.dto.request.UpdateEducationRequest;
import com.airesumeanalyzer.backend.career.education.dto.response.EducationResponse;
import com.airesumeanalyzer.backend.career.education.service.EducationService;
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
@RequestMapping(ApiRoutes.CAREER_PROFILE + "/education")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    @PostMapping
    public ResponseEntity<ApiResponse<EducationResponse>> createEducation(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @Valid @RequestBody CreateEducationRequest request
    ) {
        EducationResponse response =
                educationService.createEducation(principal.getUserId(), request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Education record created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EducationResponse>>> getEducations(
            @AuthenticationPrincipal CurrentUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        educationService.getEducations(principal.getUserId())
                )
        );
    }

    @GetMapping("/{educationId}")
    public ResponseEntity<ApiResponse<EducationResponse>> getEducation(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID educationId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        educationService.getEducation(principal.getUserId(), educationId)
                )
        );
    }

    @PutMapping("/{educationId}")
    public ResponseEntity<ApiResponse<EducationResponse>> updateEducation(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID educationId,
            @Valid @RequestBody UpdateEducationRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        educationService.updateEducation(
                                principal.getUserId(),
                                educationId,
                                request
                        ),
                        "Education record updated successfully"
                )
        );
    }

    @DeleteMapping("/{educationId}")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID educationId
    ) {
        educationService.deleteEducation(principal.getUserId(), educationId);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Education record deleted successfully")
        );
    }
}
