package org.example.placement_drive_management.repository;

import org.example.placement_drive_management.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByEmail(String email);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.email = :email")
    void deleteAllByEmail(String email);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.email = :email")
    void revokeAllByEmail(String email);

    /** Scheduled cleanup — removes tokens past expiry */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now")
    void deleteAllExpiredBefore(Instant now);

    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.email = :email AND rt.revoked = false AND rt.expiryDate > :now")
    long countActiveSessionsByEmail(String email, Instant now);
}