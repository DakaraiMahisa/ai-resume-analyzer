package com.airesumeanalyzer.backend.auth.service;

import com.airesumeanalyzer.backend.auth.dto.login.LoginRequest;
import com.airesumeanalyzer.backend.auth.dto.login.LoginResponse;
import com.airesumeanalyzer.backend.auth.dto.logout.LogoutRequest;
import com.airesumeanalyzer.backend.auth.dto.refresh.RefreshTokenRequest;
import com.airesumeanalyzer.backend.auth.dto.refresh.RefreshTokenResponse;
import com.airesumeanalyzer.backend.auth.dto.register.RegisterRequest;
import com.airesumeanalyzer.backend.auth.dto.register.RegisterResponse;
import com.airesumeanalyzer.backend.auth.entity.RefreshToken;
import com.airesumeanalyzer.backend.auth.enums.Role;
import com.airesumeanalyzer.backend.auth.repository.RefreshTokenRepository;
import com.airesumeanalyzer.backend.auth.security.CurrentUserPrincipal;
import com.airesumeanalyzer.backend.auth.security.jwt.JwtProperties;
import com.airesumeanalyzer.backend.auth.security.jwt.JwtService;
import com.airesumeanalyzer.backend.common.exception.base.CompromisedCredentialsException;
import com.airesumeanalyzer.backend.common.exception.base.ConflictException;
import com.airesumeanalyzer.backend.common.exception.base.UnauthorizedException;
import com.airesumeanalyzer.backend.user.entity.User;
import com.airesumeanalyzer.backend.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CompromisedPasswordChecker compromisedPasswordChecker;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private AuthService authService;


    @Nested
    class RegisterTests {

        @Test
        void register_shouldRegisterUserSuccessfully() {

            RegisterRequest request = new RegisterRequest(
                    " John ",
                    " Doe ",
                    " JOHN@EXAMPLE.COM ",
                    "StrongPassword123"
            );

            when(userRepository.existsByEmail("john@example.com"))
                    .thenReturn(false);

            when(compromisedPasswordChecker.check("StrongPassword123"))
                    .thenReturn(new CompromisedPasswordDecision(false));

            when(passwordEncoder.encode("StrongPassword123"))
                    .thenReturn("encoded-password");

            User savedUser = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("john@example.com")
                    .passwordHash("encoded-password")
                    .role(Role.USER)
                    .enabled(true)
                    .build();

            ReflectionTestUtils.setField(
                    savedUser,
                    "id",
                    UUID.randomUUID()
            );

            when(userRepository.save(any(User.class)))
                    .thenReturn(savedUser);

            RegisterResponse response = authService.register(request);

            assertNotNull(response);
            assertEquals(savedUser.getId(), response.userId());
            assertEquals("john@example.com", response.email());
            assertEquals("John", response.firstName());
            assertEquals("Doe", response.lastName());

            verify(userRepository).existsByEmail("john@example.com");
            verify(passwordEncoder).encode("StrongPassword123");

            ArgumentCaptor<User> userCaptor =
                    ArgumentCaptor.forClass(User.class);

            verify(userRepository).save(userCaptor.capture());

            User capturedUser = userCaptor.getValue();

            assertEquals("John", capturedUser.getFirstName());
            assertEquals("Doe", capturedUser.getLastName());
            assertEquals("john@example.com", capturedUser.getEmail());
            assertEquals("encoded-password", capturedUser.getPasswordHash());
            assertEquals(Role.USER, capturedUser.getRole());
            assertTrue(capturedUser.isEnabled());
        }

        @Test
        void register_shouldThrowConflictException_whenEmailAlreadyExists() {

            RegisterRequest request = new RegisterRequest(
                    "John",
                    "Doe",
                    "JOHN@EXAMPLE.COM",
                    "StrongPassword123"
            );

            when(userRepository.existsByEmail("john@example.com"))
                    .thenReturn(true);

            assertThrows(
                    ConflictException.class,
                    () -> authService.register(request)
            );

            verify(userRepository).existsByEmail("john@example.com");

            verifyNoInteractions(
                    compromisedPasswordChecker,
                    passwordEncoder
            );

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void register_shouldThrowCompromisedCredentialsException_whenPasswordIsCompromised() {

            RegisterRequest request = new RegisterRequest(
                    "John",
                    "Doe",
                    "john@example.com",
                    "CompromisedPassword123"
            );

            when(userRepository.existsByEmail("john@example.com"))
                    .thenReturn(false);

            when(compromisedPasswordChecker.check("CompromisedPassword123"))
                    .thenReturn(new CompromisedPasswordDecision(true));

            assertThrows(
                    CompromisedCredentialsException.class,
                    () -> authService.register(request)
            );

            verify(userRepository).existsByEmail("john@example.com");

            verify(compromisedPasswordChecker)
                    .check("CompromisedPassword123");

            verifyNoInteractions(passwordEncoder);

            verify(userRepository, never()).save(any(User.class));
        }

    }

    @Nested
    class LoginTests {

        @Test
        void login_shouldAuthenticateUserAndGenerateTokensSuccessfully() {

            UUID userId = UUID.randomUUID();

            User user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("john@example.com")
                    .passwordHash("encoded-password")
                    .role(Role.USER)
                    .enabled(true)
                    .build();

            ReflectionTestUtils.setField(user, "id", userId);

            CurrentUserPrincipal principal = new CurrentUserPrincipal(user);

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            principal.getAuthorities()
                    );

            LoginRequest request = new LoginRequest(
                    " JOHN@EXAMPLE.COM ",
                    "StrongPassword123"
            );

            when(authenticationManager.authenticate(any(Authentication.class)))
                    .thenReturn(authentication);

            when(jwtService.generateAccessToken(principal))
                    .thenReturn("access-token");

            when(jwtService.generateRefreshToken(principal))
                    .thenReturn("refresh-token");

            when(userRepository.findById(userId))
                    .thenReturn(Optional.of(user));

            when(jwtService.hashToken("refresh-token"))
                    .thenReturn("hashed-refresh-token");

            when(jwtProperties.refreshTokenExpiration())
                    .thenReturn(Duration.ofDays(7));

            when(jwtProperties.accessTokenExpiration())
                    .thenReturn(Duration.ofMinutes(15));

            LoginResponse response = authService.login(request);

            assertNotNull(response);
            assertEquals("access-token", response.accessToken());
            assertEquals("refresh-token", response.refreshToken());
            assertEquals("Bearer", response.tokenType());
            assertEquals(900L, response.expiresIn());

            verify(authenticationManager).authenticate(
                    argThat(auth ->
                            auth.getPrincipal().equals("john@example.com")
                                    && auth.getCredentials().equals("StrongPassword123")
                    )
            );

            verify(jwtService).generateAccessToken(principal);
            verify(jwtService).generateRefreshToken(principal);
            verify(userRepository).findById(userId);
            verify(jwtService).hashToken("refresh-token");

            ArgumentCaptor<RefreshToken> tokenCaptor =
                    ArgumentCaptor.forClass(RefreshToken.class);

            verify(refreshTokenRepository).save(tokenCaptor.capture());

            RefreshToken savedToken = tokenCaptor.getValue();

            assertEquals(user, savedToken.getUser());
            assertEquals("hashed-refresh-token", savedToken.getTokenHash());
            assertNotNull(savedToken.getExpiresAt());
            assertTrue(
                    savedToken.getExpiresAt().isAfter(Instant.now())
            );
        }

        @Test
        void shouldThrowUnauthorizedExceptionWhenUserIsNotFound() {

            UUID userId = UUID.randomUUID();

            User user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("john@example.com")
                    .passwordHash("encoded-password")
                    .role(Role.USER)
                    .enabled(true)
                    .build();

            ReflectionTestUtils.setField(user, "id", userId);

            CurrentUserPrincipal principal = new CurrentUserPrincipal(user);

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            principal.getAuthorities()
                    );

            LoginRequest request = new LoginRequest(
                    "john@example.com",
                    "StrongPassword123"
            );

            when(authenticationManager.authenticate(any(Authentication.class)))
                    .thenReturn(authentication);

            when(jwtService.generateAccessToken(principal))
                    .thenReturn("access-token");

            when(jwtService.generateRefreshToken(principal))
                    .thenReturn("refresh-token");

            when(userRepository.findById(userId))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UnauthorizedException.class,
                    () -> authService.login(request)
            );

            verify(authenticationManager).authenticate(any(Authentication.class));
            verify(jwtService).generateAccessToken(principal);
            verify(jwtService).generateRefreshToken(principal);
            verify(userRepository).findById(userId);

            verifyNoInteractions(refreshTokenRepository);
        }

        @Test
        void shouldPropagateAuthenticationExceptionWhenCredentialsAreInvalid() {

            LoginRequest request = new LoginRequest(
                    "john@example.com",
                    "WrongPassword123"
            );

            when(authenticationManager.authenticate(any(Authentication.class)))
                    .thenThrow(new BadCredentialsException("Invalid credentials"));

            assertThrows(
                    BadCredentialsException.class,
                    () -> authService.login(request)
            );

            verify(authenticationManager).authenticate(
                    argThat(auth ->
                            auth.getPrincipal().equals("john@example.com")
                                    && auth.getCredentials().equals("WrongPassword123")
                    )
            );

            verifyNoInteractions(jwtService);
            verifyNoInteractions(userRepository);
            verifyNoInteractions(refreshTokenRepository);
        }

    }

    @Nested
    class RefreshTests {
        @Test
        void shouldRotateRefreshTokenSuccessfully() {

            UUID userId = UUID.randomUUID();

            User user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("john@example.com")
                    .passwordHash("encoded-password")
                    .role(Role.USER)
                    .enabled(true)
                    .build();

            ReflectionTestUtils.setField(user, "id", userId);

            RefreshToken storedToken = RefreshToken.builder()
                    .user(user)
                    .tokenHash("old-token-hash")
                    .expiresAt(Instant.now().plus(Duration.ofDays(7)))
                    .build();

            RefreshTokenRequest request =
                    new RefreshTokenRequest("old-refresh-token");

            when(jwtService.validateToken("old-refresh-token"))
                    .thenReturn(true);

            when(jwtService.isRefreshToken("old-refresh-token"))
                    .thenReturn(true);

            when(jwtService.hashToken("old-refresh-token"))
                    .thenReturn("old-token-hash");

            when(refreshTokenRepository
                    .findByTokenHashAndRevokedAtIsNull("old-token-hash"))
                    .thenReturn(Optional.of(storedToken));

            when(jwtService.generateAccessToken(any(CurrentUserPrincipal.class)))
                    .thenReturn("new-access-token");

            when(jwtService.generateRefreshToken(any(CurrentUserPrincipal.class)))
                    .thenReturn("new-refresh-token");

            when(jwtService.hashToken("new-refresh-token"))
                    .thenReturn("new-token-hash");

            when(jwtProperties.refreshTokenExpiration())
                    .thenReturn(Duration.ofDays(7));

            when(jwtProperties.accessTokenExpiration())
                    .thenReturn(Duration.ofMinutes(15));


            RefreshTokenResponse response = authService.refresh(request);

            assertNotNull(response);
            assertEquals("new-access-token", response.accessToken());
            assertEquals("new-refresh-token", response.refreshToken());
            assertEquals("Bearer", response.tokenType());
            assertEquals(900L, response.expiresIn());

            assertNotNull(storedToken.getRevokedAt());

            ArgumentCaptor<RefreshToken> tokenCaptor =
                    ArgumentCaptor.forClass(RefreshToken.class);

            verify(refreshTokenRepository, times(2))
                    .save(tokenCaptor.capture());

            List<RefreshToken> savedTokens = tokenCaptor.getAllValues();

            assertSame(storedToken, savedTokens.get(0));
            assertEquals(user, savedTokens.get(1).getUser());
            assertEquals("new-token-hash", savedTokens.get(1).getTokenHash());
            assertNotNull(savedTokens.get(1).getExpiresAt());

            assertTrue(
                    savedTokens.get(1).getExpiresAt().isAfter(Instant.now())
            );

            verify(jwtService).generateAccessToken(any(CurrentUserPrincipal.class));
            verify(jwtService).generateRefreshToken(any(CurrentUserPrincipal.class));
        }

        @Test
        void shouldThrowUnauthorizedExceptionWhenRefreshTokenIsInvalid() {

            RefreshTokenRequest request =
                    new RefreshTokenRequest("invalid-token");

            when(jwtService.validateToken("invalid-token"))
                    .thenReturn(false);

            assertThrows(
                    UnauthorizedException.class,
                    () -> authService.refresh(request)
            );

            verify(jwtService).validateToken("invalid-token");

            verify(jwtService, never()).isRefreshToken(anyString());
            verifyNoInteractions(refreshTokenRepository);
        }

        @Test
        void shouldThrowUnauthorizedExceptionWhenTokenIsNotRefreshToken() {

            RefreshTokenRequest request =
                    new RefreshTokenRequest("access-token");

            when(jwtService.validateToken("access-token"))
                    .thenReturn(true);

            when(jwtService.isRefreshToken("access-token"))
                    .thenReturn(false);

            assertThrows(
                    UnauthorizedException.class,
                    () -> authService.refresh(request)
            );

            verify(jwtService).validateToken("access-token");
            verify(jwtService).isRefreshToken("access-token");

            verify(jwtService, never()).hashToken(anyString());
            verifyNoInteractions(refreshTokenRepository);
        }

        @Test
        void shouldThrowUnauthorizedExceptionWhenStoredRefreshTokenIsNotFound() {


            RefreshTokenRequest request =
                    new RefreshTokenRequest("valid-refresh-token");

            when(jwtService.validateToken("valid-refresh-token"))
                    .thenReturn(true);

            when(jwtService.isRefreshToken("valid-refresh-token"))
                    .thenReturn(true);

            when(jwtService.hashToken("valid-refresh-token"))
                    .thenReturn("token-hash");

            when(refreshTokenRepository
                    .findByTokenHashAndRevokedAtIsNull("token-hash"))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UnauthorizedException.class,
                    () -> authService.refresh(request)
            );

            verify(refreshTokenRepository)
                    .findByTokenHashAndRevokedAtIsNull("token-hash");

            verify(jwtService, never())
                    .generateAccessToken(any(CurrentUserPrincipal.class));

            verify(jwtService, never())
                    .generateRefreshToken(any(CurrentUserPrincipal.class));

            verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
        }

        @Test
        void shouldThrowUnauthorizedExceptionWhenStoredRefreshTokenIsExpired() {


            User user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("john@example.com")
                    .passwordHash("encoded-password")
                    .role(Role.USER)
                    .enabled(true)
                    .build();

            RefreshToken storedToken = RefreshToken.builder()
                    .user(user)
                    .tokenHash("token-hash")
                    .expiresAt(Instant.now().minusSeconds(60))
                    .build();

            RefreshTokenRequest request =
                    new RefreshTokenRequest("expired-refresh-token");

            when(jwtService.validateToken("expired-refresh-token"))
                    .thenReturn(true);

            when(jwtService.isRefreshToken("expired-refresh-token"))
                    .thenReturn(true);

            when(jwtService.hashToken("expired-refresh-token"))
                    .thenReturn("token-hash");

            when(refreshTokenRepository
                    .findByTokenHashAndRevokedAtIsNull("token-hash"))
                    .thenReturn(Optional.of(storedToken));


            assertThrows(
                    UnauthorizedException.class,
                    () -> authService.refresh(request)
            );

            verify(refreshTokenRepository)
                    .findByTokenHashAndRevokedAtIsNull("token-hash");

            verify(jwtService, never())
                    .generateAccessToken(any(CurrentUserPrincipal.class));

            verify(jwtService, never())
                    .generateRefreshToken(any(CurrentUserPrincipal.class));

            verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
        }

        @Test
        void shouldThrowUnauthorizedExceptionWhenUserAccountIsDisabled() {

            User user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("john@example.com")
                    .passwordHash("encoded-password")
                    .role(Role.USER)
                    .enabled(false)
                    .build();

            RefreshToken storedToken = RefreshToken.builder()
                    .user(user)
                    .tokenHash("token-hash")
                    .expiresAt(Instant.now().plus(Duration.ofDays(7)))
                    .build();

            RefreshTokenRequest request =
                    new RefreshTokenRequest("valid-refresh-token");

            when(jwtService.validateToken("valid-refresh-token"))
                    .thenReturn(true);

            when(jwtService.isRefreshToken("valid-refresh-token"))
                    .thenReturn(true);

            when(jwtService.hashToken("valid-refresh-token"))
                    .thenReturn("token-hash");

            when(refreshTokenRepository
                    .findByTokenHashAndRevokedAtIsNull("token-hash"))
                    .thenReturn(Optional.of(storedToken));

            assertThrows(
                    UnauthorizedException.class,
                    () -> authService.refresh(request)
            );

            verify(jwtService, never())
                    .generateAccessToken(any(CurrentUserPrincipal.class));

            verify(jwtService, never())
                    .generateRefreshToken(any(CurrentUserPrincipal.class));

            verify(refreshTokenRepository, never())
                    .save(any(RefreshToken.class));
        }
    }

    @Nested
    class LogoutTests {

        @Test
        void shouldRevokeRefreshTokenSuccessfully() {

            User user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("john@example.com")
                    .passwordHash("encoded-password")
                    .role(Role.USER)
                    .enabled(true)
                    .build();

            RefreshToken storedToken = RefreshToken.builder()
                    .user(user)
                    .tokenHash("token-hash")
                    .expiresAt(Instant.now().plus(Duration.ofDays(7)))
                    .build();

            LogoutRequest request =
                    new LogoutRequest("valid-refresh-token");

            when(jwtService.validateToken("valid-refresh-token"))
                    .thenReturn(true);

            when(jwtService.isRefreshToken("valid-refresh-token"))
                    .thenReturn(true);

            when(jwtService.hashToken("valid-refresh-token"))
                    .thenReturn("token-hash");

            when(refreshTokenRepository
                    .findByTokenHashAndRevokedAtIsNull("token-hash"))
                    .thenReturn(Optional.of(storedToken));

            authService.logout(request);


            assertNotNull(storedToken.getRevokedAt());

            ArgumentCaptor<RefreshToken> tokenCaptor =
                    ArgumentCaptor.forClass(RefreshToken.class);

            verify(refreshTokenRepository).save(tokenCaptor.capture());

            assertSame(storedToken, tokenCaptor.getValue());
            assertNotNull(tokenCaptor.getValue().getRevokedAt());
        }

        @Test
        void shouldThrowUnauthorizedExceptionWhenTokenIsNotRefreshToken() {

            LogoutRequest request =
                    new LogoutRequest("access-token");

            when(jwtService.validateToken("access-token"))
                    .thenReturn(true);

            when(jwtService.isRefreshToken("access-token"))
                    .thenReturn(false);

            assertThrows(
                    UnauthorizedException.class,
                    () -> authService.logout(request)
            );

            verify(jwtService).validateToken("access-token");
            verify(jwtService).isRefreshToken("access-token");

            verify(jwtService, never()).hashToken(anyString());
            verifyNoInteractions(refreshTokenRepository);
        }

        @Test
        void shouldThrowUnauthorizedExceptionWhenStoredTokenIsNotFound() {

            LogoutRequest request =
                    new LogoutRequest("valid-refresh-token");

            when(jwtService.validateToken("valid-refresh-token"))
                    .thenReturn(true);

            when(jwtService.isRefreshToken("valid-refresh-token"))
                    .thenReturn(true);

            when(jwtService.hashToken("valid-refresh-token"))
                    .thenReturn("token-hash");

            when(refreshTokenRepository
                    .findByTokenHashAndRevokedAtIsNull("token-hash"))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UnauthorizedException.class,
                    () -> authService.logout(request)
            );

            verify(refreshTokenRepository)
                    .findByTokenHashAndRevokedAtIsNull("token-hash");

            verify(refreshTokenRepository, never())
                    .save(any(RefreshToken.class));
        }

        @Test
        void shouldThrowUnauthorizedExceptionWhenStoredTokenIsExpired() {

            User user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("john@example.com")
                    .passwordHash("encoded-password")
                    .role(Role.USER)
                    .enabled(true)
                    .build();

            RefreshToken storedToken = RefreshToken.builder()
                    .user(user)
                    .tokenHash("token-hash")
                    .expiresAt(Instant.now().minusSeconds(60))
                    .build();

            LogoutRequest request =
                    new LogoutRequest("expired-refresh-token");

            when(jwtService.validateToken("expired-refresh-token"))
                    .thenReturn(true);

            when(jwtService.isRefreshToken("expired-refresh-token"))
                    .thenReturn(true);

            when(jwtService.hashToken("expired-refresh-token"))
                    .thenReturn("token-hash");

            when(refreshTokenRepository
                    .findByTokenHashAndRevokedAtIsNull("token-hash"))
                    .thenReturn(Optional.of(storedToken));

            assertThrows(
                    UnauthorizedException.class,
                    () -> authService.logout(request)
            );

            assertNull(storedToken.getRevokedAt());

            verify(refreshTokenRepository, never())
                    .save(any(RefreshToken.class));
        }
    }
}
