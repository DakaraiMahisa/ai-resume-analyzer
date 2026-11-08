package com.airesumeanalyzer.backend.auth.controller;

import com.airesumeanalyzer.backend.auth.dto.login.LoginRequest;
import com.airesumeanalyzer.backend.auth.dto.login.LoginResponse;
import com.airesumeanalyzer.backend.auth.dto.logout.LogoutRequest;
import com.airesumeanalyzer.backend.auth.dto.refresh.RefreshTokenRequest;
import com.airesumeanalyzer.backend.auth.dto.refresh.RefreshTokenResponse;
import com.airesumeanalyzer.backend.auth.dto.register.RegisterRequest;
import com.airesumeanalyzer.backend.auth.dto.register.RegisterResponse;
import com.airesumeanalyzer.backend.auth.security.jwt.JwtService;
import com.airesumeanalyzer.backend.auth.service.AuthService;
import com.airesumeanalyzer.backend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {

        RegisterRequest request = new RegisterRequest(
                "John",
                "Doe",
                "john@example.com",
                "StrongPassword123"
        );

        RegisterResponse response = new RegisterResponse(
                UUID.randomUUID(),
                "john@example.com",
                "John",
                "Doe"
        );

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "firstName": "John",
                                "lastName": "Doe",
                                "email": "john@example.com",
                                "password": "StrongPassword123"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Registration successful"))
                .andExpect(jsonPath("$.data.email")
                        .value("john@example.com"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenRegistrationRequestIsInvalid() throws Exception {

        mockMvc.perform(post("/api/v1/auth/register/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "firstName": "",
                    "lastName": "Doe",
                    "email": "invalid-email",
                    "password": "123"
                }
                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void shouldLoginUserSuccessfully() throws Exception {

        LoginResponse response = new LoginResponse(
                "access-token",
                "refresh-token",
                "Bearer",
                900
        );

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "email": "john@example.com",
                    "password": "StrongPassword123"
                }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(900));

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void shouldRefreshTokenSuccessfully() throws Exception {

        RefreshTokenResponse response = new RefreshTokenResponse(
                "new-access-token",
                "new-refresh-token",
                "Bearer",
                900
        );

        when(authService.refresh(any(RefreshTokenRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/refresh/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "refreshToken": "old-refresh-token"
                }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("new-refresh-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(900));

        verify(authService).refresh(any(RefreshTokenRequest.class));
    }

    @Test
    void shouldLogoutUserSuccessfully() throws Exception {

        doNothing().when(authService).logout(any(LogoutRequest.class));

        mockMvc.perform(post("/api/v1/auth/logout/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "refreshToken": "refresh-token"
                }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Logout successful"));

        verify(authService).logout(any(LogoutRequest.class));
    }
}
