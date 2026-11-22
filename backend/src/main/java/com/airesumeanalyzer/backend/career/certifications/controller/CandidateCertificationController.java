package com.airesumeanalyzer.backend.career.certifications.controller;



import com.airesumeanalyzer.backend.auth.security.CurrentUserPrincipal;
import com.airesumeanalyzer.backend.career.certifications.dto.response.CandidateCertificationResponse;
import com.airesumeanalyzer.backend.career.certifications.dto.request.CreateCandidateCertificationRequest;
import com.airesumeanalyzer.backend.career.certifications.dto.request.UpdateCandidateCertificationRequest;
import com.airesumeanalyzer.backend.career.certifications.service.CandidateCertificationService;
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
@RequestMapping(ApiRoutes.CAREER_PROFILE + "/certifications")
@RequiredArgsConstructor
public class CandidateCertificationController {

    private final CandidateCertificationService certificationService;

    @PostMapping
    public ResponseEntity<ApiResponse<CandidateCertificationResponse>> create(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @Valid @RequestBody CreateCandidateCertificationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        certificationService.create(principal.getUserId(), request)
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CandidateCertificationResponse>>> getAll(
            @AuthenticationPrincipal CurrentUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(certificationService.getAll(principal.getUserId()))
        );
    }

    @GetMapping("/{certificationId}")
    public ResponseEntity<ApiResponse<CandidateCertificationResponse>> getById(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID certificationId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        certificationService.getById(principal.getUserId(), certificationId)
                )
        );
    }

    @PutMapping("/{certificationId}")
    public ResponseEntity<ApiResponse<CandidateCertificationResponse>> update(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID certificationId,
            @Valid @RequestBody UpdateCandidateCertificationRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        certificationService.update(
                                principal.getUserId(),
                                certificationId,
                                request
                        )
                )
        );
    }

    @DeleteMapping("/{certificationId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID certificationId
    ) {
        certificationService.delete(principal.getUserId(), certificationId);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Certification deleted successfully")
        );
    }
}
