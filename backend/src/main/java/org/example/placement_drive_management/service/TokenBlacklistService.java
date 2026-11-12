package org.example.placement_drive_management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.placement_drive_management.entity.TokenBlacklist;
import org.example.placement_drive_management.repository.TokenBlacklistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Transactional
    public void blacklist(String token, Instant expiry) {
        if (!tokenBlacklistRepository.existsByToken(token)) {
            tokenBlacklistRepository.save(
                    TokenBlacklist.builder()
                            .token(token)
                            .expiryDate(expiry)
                            .build());
            log.debug("Access token added to blacklist");
        }
    }

    @Transactional(readOnly = true)
    public boolean isBlacklisted(String token) {
        return tokenBlacklistRepository.existsByToken(token);
    }

    @Transactional
    public void purgeExpired() {
        tokenBlacklistRepository.deleteAllExpiredBefore(Instant.now());
    }
}