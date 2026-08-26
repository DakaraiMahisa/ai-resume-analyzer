package com.airesumeanalyzer.backend.auth.repository;

import com.airesumeanalyzer.backend.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    Optional<RefreshToken> findByTokenHashAndRevokedAtIsNull(
            String tokenHash
    );

    List<RefreshToken> findAllByUserId(UUID userId);

    void deleteAllByUserId(UUID userId);
}