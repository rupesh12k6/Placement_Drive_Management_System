package org.example.placement_drive_management.repository;

import org.example.placement_drive_management.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {

    boolean existsByToken(String token);

    @Modifying
    @Query("DELETE FROM TokenBlacklist tb WHERE tb.expiryDate < :now")
    void deleteAllExpiredBefore(Instant now);
}