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

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody LogoutRequest request
    ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}