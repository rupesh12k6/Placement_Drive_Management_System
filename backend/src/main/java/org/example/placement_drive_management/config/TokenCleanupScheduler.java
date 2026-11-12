package org.example.placement_drive_management.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.placement_drive_management.service.RefreshTokenService;
import org.example.placement_drive_management.service.TokenBlacklistService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Runs nightly to purge:
 *  - Expired refresh tokens from refresh_tokens table
 *  - Expired access tokens from token_blacklist table
 *
 * This keeps both tables lean without affecting active sessions.
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupScheduler {

    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    /** Every day at 2:00 AM */
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupExpiredTokens() {
        log.info("Starting scheduled token cleanup...");
        refreshTokenService.purgeExpiredTokens();
        tokenBlacklistService.purgeExpired();
        log.info("Token cleanup completed.");
    }
}