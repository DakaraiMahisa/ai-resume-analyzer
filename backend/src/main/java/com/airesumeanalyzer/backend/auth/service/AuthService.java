package com.airesumeanalyzer.backend.auth.service;

import com.airesumeanalyzer.backend.auth.dto.login.LoginRequest;
import com.airesumeanalyzer.backend.auth.dto.login.LoginResponse;
import com.airesumeanalyzer.backend.auth.dto.refresh.RefreshTokenRequest;
import com.airesumeanalyzer.backend.auth.dto.refresh.RefreshTokenResponse;
import com.airesumeanalyzer.backend.auth.dto.register.RegisterRequest;
import com.airesumeanalyzer.backend.auth.dto.register.RegisterResponse;
import com.airesumeanalyzer.backend.auth.dto.logout.LogoutRequest;
import com.airesumeanalyzer.backend.auth.entity.RefreshToken;
import com.airesumeanalyzer.backend.user.entity.User;
import com.airesumeanalyzer.backend.auth.enums.Role;
import com.airesumeanalyzer.backend.auth.repository.RefreshTokenRepository;
import com.airesumeanalyzer.backend.user.repository.UserRepository;
import com.airesumeanalyzer.backend.auth.security.CurrentUserPrincipal;
import com.airesumeanalyzer.backend.auth.security.jwt.JwtProperties;
import com.airesumeanalyzer.backend.auth.security.jwt.JwtService;
import com.airesumeanalyzer.backend.common.exception.base.CompromisedCredentialsException;
import com.airesumeanalyzer.backend.common.exception.base.ConflictException;
import com.airesumeanalyzer.backend.common.exception.base.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CompromisedPasswordChecker compromisedPasswordChecker;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public LoginResponse login(LoginRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                request.password()
                        )
                );

        CurrentUserPrincipal principal =
                (CurrentUserPrincipal) authentication.getPrincipal();

        String accessToken =
                jwtService.generateAccessToken(principal);

        String refreshToken =
                jwtService.generateRefreshToken(principal);
        User user = userRepository.findById(principal.getUserId())
                .orElseThrow(() ->
                        new UnauthorizedException("User not found")
                );

        RefreshToken storedToken = RefreshToken.builder()
                .user(user)
                .tokenHash(jwtService.hashToken(refreshToken))
                .expiresAt(
                        Instant.now()
                                .plus(jwtProperties.refreshTokenExpiration())
                )
                .build();

        refreshTokenRepository.save(storedToken);
        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtProperties.accessTokenExpiration().toSeconds()
        );
    }

    @Transactional
    public RefreshTokenResponse refresh(RefreshTokenRequest request) {

        String rawRefreshToken = request.refreshToken();

        if (!jwtService.validateToken(rawRefreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        if (!jwtService.isRefreshToken(rawRefreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }


        String tokenHash = jwtService.hashToken(rawRefreshToken);

        RefreshToken storedToken =
                refreshTokenRepository
                        .findByTokenHashAndRevokedAtIsNull(tokenHash)
                        .orElseThrow(() ->
                                new UnauthorizedException(
                                        "Invalid refresh token"
                                )
                        );

        if (storedToken.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Invalid refresh token");
        }


        User user = storedToken.getUser();

        if (!user.isEnabled()) {
            throw new UnauthorizedException("User account is disabled");
        }


        CurrentUserPrincipal principal =
                new CurrentUserPrincipal(user);


        String newAccessToken =
                jwtService.generateAccessToken(principal);

        String newRefreshToken =
                jwtService.generateRefreshToken(principal);


        storedToken.setRevokedAt(Instant.now());

        RefreshToken newStoredToken = RefreshToken.builder()
                .user(user)
                .tokenHash(jwtService.hashToken(newRefreshToken))
                .expiresAt(
                        Instant.now()
                                .plus(jwtProperties.refreshTokenExpiration())
                )
                .build();

        refreshTokenRepository.save(storedToken);
        refreshTokenRepository.save(newStoredToken);

        /*
         * 11. Return the new credentials.
         */
        return new RefreshTokenResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                jwtProperties.accessTokenExpiration().toSeconds()
        );
    }

    public RegisterResponse register(RegisterRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(
                    "An account with this email already exists"
            );
        }
        CompromisedPasswordDecision decision =
                compromisedPasswordChecker.check(request.password());

        if (decision.isCompromised()) {
            throw new CompromisedCredentialsException(
                    "The password has been exposed in a known data breach. Please choose a different password."
            );
        }
        User user = User.builder()
                .firstName(request.firstName().trim())
                .lastName(request.lastName().trim())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName()
        );
    }

    @Transactional
    public void logout(LogoutRequest request) {

        String rawRefreshToken = request.refreshToken();

        if (!jwtService.validateToken(rawRefreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        if (!jwtService.isRefreshToken(rawRefreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        String tokenHash = jwtService.hashToken(rawRefreshToken);

        RefreshToken storedToken =
                refreshTokenRepository
                        .findByTokenHashAndRevokedAtIsNull(tokenHash)
                        .orElseThrow(() ->
                                new UnauthorizedException(
                                        "Invalid refresh token"
                                )
                        );

        if (storedToken.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        storedToken.setRevokedAt(Instant.now());

        refreshTokenRepository.save(storedToken);
    }
}