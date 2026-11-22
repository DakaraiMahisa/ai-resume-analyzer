package com.airesumeanalyzer.backend.career.skills.controller;

import com.airesumeanalyzer.backend.auth.security.CurrentUserPrincipal;
import com.airesumeanalyzer.backend.career.skills.dto.request.CreateCandidateSkillRequest;
import com.airesumeanalyzer.backend.career.skills.dto.request.UpdateCandidateSkillRequest;
import com.airesumeanalyzer.backend.career.skills.dto.response.CandidateSkillResponse;
import com.airesumeanalyzer.backend.career.skills.service.CandidateSkillService;
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
@RequestMapping(ApiRoutes.CAREER_PROFILE + "/skills")
@RequiredArgsConstructor
public class CandidateSkillController {

    private final CandidateSkillService candidateSkillService;

    @PostMapping
    public ResponseEntity<ApiResponse<CandidateSkillResponse>> createSkill(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @Valid @RequestBody CreateCandidateSkillRequest request
    ) {
        CandidateSkillResponse response = candidateSkillService.createSkill(
                principal.getUserId(), request
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Skill added successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CandidateSkillResponse>>> getSkills(
            @AuthenticationPrincipal CurrentUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        candidateSkillService.getSkills(principal.getUserId()),
                        "Skills fetched successfully"
                )
        );
    }

    @GetMapping("/{skillId}")
    public ResponseEntity<ApiResponse<CandidateSkillResponse>> getSkill(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID skillId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        candidateSkillService.getSkill(
                                principal.getUserId(), skillId
                        ),
                        "Skill fetched successfully"
                )
        );
    }

    @PutMapping("/{skillId}")
    public ResponseEntity<ApiResponse<CandidateSkillResponse>> updateSkill(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID skillId,
            @Valid @RequestBody UpdateCandidateSkillRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        candidateSkillService.updateSkill(
                                principal.getUserId(), skillId, request
                        ),
                        "Skill updated successfully"
                )
        );
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<ApiResponse<Void>> deleteSkill(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID skillId
    ) {
        candidateSkillService.deleteSkill(principal.getUserId(), skillId);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Skill deleted successfully")
        );
    }
}
