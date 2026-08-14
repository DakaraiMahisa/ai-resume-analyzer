package com.airesumeanalyzer.backend.auth.service;

import com.airesumeanalyzer.backend.auth.dto.login.LoginRequest;
import com.airesumeanalyzer.backend.auth.dto.login.LoginResponse;
import com.airesumeanalyzer.backend.auth.dto.refresh.RefreshTokenRequest;
import com.airesumeanalyzer.backend.auth.dto.refresh.RefreshTokenResponse;
import com.airesumeanalyzer.backend.auth.dto.register.RegisterRequest;
import com.airesumeanalyzer.backend.auth.dto.register.RegisterResponse;
import com.airesumeanalyzer.backend.auth.dto.logout.LogoutRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    public LoginResponse login(LoginRequest request) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public RegisterResponse register(RegisterRequest request) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void logout(LogoutRequest request) {
        throw new UnsupportedOperationException("Not implemented");
    }
}