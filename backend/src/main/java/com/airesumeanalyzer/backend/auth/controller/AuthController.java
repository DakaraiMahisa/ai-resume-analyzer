package com.airesumeanalyzer.backend.auth.controller;

import com.airesumeanalyzer.backend.auth.dto.login.LoginRequest;
import com.airesumeanalyzer.backend.auth.dto.login.LoginResponse;
import com.airesumeanalyzer.backend.auth.dto.logout.LogoutRequest;
import com.airesumeanalyzer.backend.auth.dto.refresh.RefreshTokenRequest;
import com.airesumeanalyzer.backend.auth.dto.refresh.RefreshTokenResponse;
import com.airesumeanalyzer.backend.auth.dto.register.RegisterRequest;
import com.airesumeanalyzer.backend.auth.dto.register.RegisterResponse;
import com.airesumeanalyzer.backend.auth.service.AuthService;
import com.airesumeanalyzer.backend.common.api.ApiResponse;
import com.airesumeanalyzer.backend.common.api.ApiRoutes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiRoutes.AUTH)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login/public")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Login successful")
        );
    }

    @PostMapping("/refresh/public")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        RefreshTokenResponse response = authService.refresh(request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Token refreshed successfully")
        );
    }

    @PostMapping("/register/public")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        RegisterResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(response, "Registration successful")
        );
    }

    @PostMapping("/logout/public")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody LogoutRequest request
    ) {
        authService.logout(request);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Logout successful")
        );
    }
}