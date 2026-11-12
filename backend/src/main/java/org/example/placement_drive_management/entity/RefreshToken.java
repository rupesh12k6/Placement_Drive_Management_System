package org.example.placement_drive_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Stores active refresh tokens.
 * One row per device per user — supports multi-device sessions.
 * Marked revoked=true on logout or rotation (keeps audit trail).
 */
@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_rt_email",  columnList = "email"),
                @Index(name = "idx_rt_token",  columnList = "token")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Owner's email — cross-entity (Student or Admin) */
    @Column(nullable = false)
    private String email;

    /** Role at time of issue — re-read from DB on each rotation */
    @Column(nullable = false)
    private String role;

    /** UUID token value sent to the client */
    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    /** Optional: browser/device identifier for multi-device tracking */
    private String deviceInfo;

    @Builder.Default
    @Column(nullable = false)
    private boolean revoked = false;

    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }
}