package org.example.placement_drive_management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.placement_drive_management.entity.RefreshToken;
import org.example.placement_drive_management.exceptions.TokenExpiredException;
import org.example.placement_drive_management.exceptions.TokenRevokedException;
import org.example.placement_drive_management.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMs;

    // ── Create ───────────────────────────────────────────────────

    @Transactional
    public RefreshToken createRefreshToken(String email, String role, String deviceInfo) {
        RefreshToken token = RefreshToken.builder()
                .email(email)
                .role(role)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpirationMs))
                .deviceInfo(deviceInfo)
                .revoked(false)
                .build();
        return refreshTokenRepository.save(token);
    }

    // ── Verify ───────────────────────────────────────────────────

    /**
     * Validate the refresh token.
     * - Not found / missing  → TokenRevokedException
     * - Revoked flag set     → replay attack suspected → revoke ALL user tokens
     * - Expired              → delete & throw TokenExpiredException
     */
    @Transactional
    public RefreshToken verifyRefreshToken(String tokenValue) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new TokenRevokedException(
                        "Refresh token not found or already revoked. Please log in again."));

        if (token.isRevoked()) {
            // Possible replay attack — nuke all sessions for this user
            log.warn("Revoked refresh token reuse detected for: {}", token.getEmail());
            refreshTokenRepository.revokeAllByEmail(token.getEmail());
            throw new TokenRevokedException(
                    "Security violation detected. All sessions invalidated. Please log in again.");
        }

        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            log.info("Expired refresh token removed for: {}", token.getEmail());
            throw new TokenExpiredException("Refresh token has expired. Please log in again.");
        }

        return token;
    }

    // ── Rotate ───────────────────────────────────────────────────

    /**
     * Refresh token rotation:
     * Revoke old token → issue new token.
     * Prevents replay attacks even if a token is stolen.
     */
    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken oldToken) {
        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);
        return createRefreshToken(oldToken.getEmail(), oldToken.getRole(), oldToken.getDeviceInfo());
    }

    // ── Revoke ───────────────────────────────────────────────────

    @Transactional
    public void revokeToken(String tokenValue) {
        refreshTokenRepository.findByToken(tokenValue).ifPresent(t -> {
            t.setRevoked(true);
            refreshTokenRepository.save(t);
            log.info("Refresh token revoked for: {}", t.getEmail());
        });
    }

    @Transactional
    public void revokeAllForUser(String email) {
        refreshTokenRepository.revokeAllByEmail(email);
        log.info("All refresh tokens revoked for: {}", email);
    }

    // ── Cleanup (called from scheduler) ──────────────────────────

    @Transactional
    public void purgeExpiredTokens() {
        refreshTokenRepository.deleteAllExpiredBefore(Instant.now());
    }
}