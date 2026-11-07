package com.airesumeanalyzer.backend.auth.security;

import com.airesumeanalyzer.backend.auth.enums.Role;
import com.airesumeanalyzer.backend.auth.security.jwt.JwtProperties;
import com.airesumeanalyzer.backend.auth.security.jwt.JwtService;
import com.airesumeanalyzer.backend.common.enums.DocumentProcessingStatus;
import com.airesumeanalyzer.backend.config.TestcontainersConfiguration;
import com.airesumeanalyzer.backend.resume.entity.Resume;
import com.airesumeanalyzer.backend.resume.repository.ResumeRepository;
import com.airesumeanalyzer.backend.user.entity.User;
import com.airesumeanalyzer.backend.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AuthSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeRepository resumeRepository;


    @Test
    void shouldRejectRequestToProtectedEndpointWithoutJwt() throws Exception {

        mockMvc.perform(get("/api/v1/resumes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectRequestToProtectedEndpointWithInvalidJwt() throws Exception {

        mockMvc.perform(get("/api/v1/resumes")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectRequestToProtectedEndpointWithExpiredJwt() throws Exception {

        String expiredToken = generateExpiredToken();

        mockMvc.perform(get("/api/v1/resumes")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowRequestToProtectedEndpointWithValidJwt() throws Exception {

        User user = userRepository.saveAndFlush(
                User.builder()
                        .firstName("Test")
                        .lastName("User")
                        .email("security-" + UUID.randomUUID() + "@example.com")
                        .passwordHash("not-a-real-password-hash")
                        .role(Role.USER)
                        .enabled(true)
                        .build()
        );

        CurrentUserPrincipal principal = new CurrentUserPrincipal(user);


        String token = jwtService.generateAccessToken(principal);

        // Act & Assert
        mockMvc.perform(get("/api/v1/resumes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowUserToRetrieveTheirOwnResume() throws Exception {
        User owner = createTestUser();
        Resume resume = createTestResume(owner);

        String token = generateAccessToken(owner);

        mockMvc.perform(get("/api/v1/resumes/{id}", resume.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldPreventUserFromRetrievingAnotherUsersResume() throws Exception {
        User owner = createTestUser();
        User anotherUser = createTestUser();

        Resume resume = createTestResume(anotherUser);

        String token = generateAccessToken(owner);

        mockMvc.perform(get("/api/v1/resumes/{id}", resume.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldPreventUserFromDeletingAnotherUsersResume() throws Exception {
        User owner = createTestUser();
        User anotherUser = createTestUser();

        Resume resume = createTestResume(anotherUser);

        String token = generateAccessToken(owner);

        mockMvc.perform(delete("/api/v1/resumes/{id}", resume.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

        assertTrue(resumeRepository.existsById(resume.getId()));
    }

    @Test
    void shouldReturnOnlyAuthenticatedUsersResumes() throws Exception {
        User userA = createTestUser();
        User userB = createTestUser();

        Resume userAResume = createTestResume(userA);
        createTestResume(userB);

        String token = generateAccessToken(userA);

        mockMvc.perform(get("/api/v1/resumes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id")
                        .value(userAResume.getId().toString()));
    }

    private String generateExpiredToken() {

        Instant now = Instant.now();

        SecretKey signingKey = Keys.hmacShaKeyFor(
                HexFormat.of().parseHex(jwtProperties.secret())
        );

        return Jwts.builder()
                .subject(UUID.randomUUID().toString())
                .issuedAt(Date.from(now.minusSeconds(120)))
                .expiration(Date.from(now.minusSeconds(60)))
                .signWith(signingKey)
                .compact();
    }

    private User createTestUser() {
        return userRepository.saveAndFlush(
                User.builder()
                        .firstName("Test")
                        .lastName("User")
                        .email("security-" + UUID.randomUUID() + "@example.com")
                        .passwordHash("not-a-real-password-hash")
                        .role(Role.USER)
                        .enabled(true)
                        .build()
        );
    }

    private Resume createTestResume(User owner) {
        return resumeRepository.saveAndFlush(
                Resume.builder()
                        .owner(owner)
                        .originalFilename("test-resume.pdf")
                        .storagePath("test/" + UUID.randomUUID() + ".pdf")
                        .checksum(UUID.randomUUID().toString().replace("-", "")
                                .repeat(2))
                        .processingStatus(DocumentProcessingStatus.UPLOADED)
                        .build()
        );
    }

    private String generateAccessToken(User user) {
        return jwtService.generateAccessToken(
                new CurrentUserPrincipal(user)
        );
    }
}