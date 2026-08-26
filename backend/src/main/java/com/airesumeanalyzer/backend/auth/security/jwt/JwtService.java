package com.airesumeanalyzer.backend.auth.security.jwt;

import com.airesumeanalyzer.backend.auth.repository.UserRepository;
import com.airesumeanalyzer.backend.auth.security.CurrentUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String TOKEN_TYPE_CLAIM = "token_type";

    private static final String ACCESS_TOKEN = "access";
    private static final String REFRESH_TOKEN = "refresh";

    private final JwtProperties jwtProperties;
    private SecretKey signingKey;

    @PostConstruct
    void initializeSigningKey() {

        byte[] keyBytes =
                Decoders.BASE64.decode(jwtProperties.secret());

        signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(CurrentUserPrincipal principal) {

        return generateToken(
                principal,
                jwtProperties.accessTokenExpiration(),
                ACCESS_TOKEN
        );
    }

    public String generateRefreshToken(CurrentUserPrincipal principal) {

        return generateToken(
                principal,
                jwtProperties.refreshTokenExpiration(),
                REFRESH_TOKEN
        );
    }

    private String generateToken(
            CurrentUserPrincipal principal,
            java.time.Duration expiration,
            String tokenType
    ) {

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(expiration);

        return Jwts.builder()
                .subject(principal.getUserId().toString())
                .claim("email", principal.getUsername())
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();
    }

    public boolean validateToken(String token) {

        try {
            getClaims(token);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is not available",
                    e
            );
        }
    }

    public String extractUserId(String token) {

        return getClaims(token)
                .getSubject();
    }

    public String extractEmail(String token) {

        return getClaims(token)
                .get("email", String.class);
    }

    public boolean isAccessToken(String token) {

        return ACCESS_TOKEN.equals(
                getClaims(token).get(TOKEN_TYPE_CLAIM, String.class)
        );
    }

    public boolean isRefreshToken(String token) {

        return REFRESH_TOKEN.equals(
                getClaims(token).get(TOKEN_TYPE_CLAIM, String.class)
        );
    }

    private Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}