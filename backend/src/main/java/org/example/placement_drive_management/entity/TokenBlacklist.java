
package org.example.placement_drive_management.entity;

import jakarta.persistence.*;
        import lombok.*;

        import java.time.Instant;

/**
 * Holds invalidated access tokens until their natural expiry.
 * Checked in JwtAuthFilter on every request after logout.
 * Nightly cleanup job purges rows past their expiry date.
 */
@Entity
@Table(
        name = "token_blacklist",
        indexes = {
                @Index(name = "idx_bl_token",  columnList = "token"),
                @Index(name = "idx_bl_expiry", columnList = "expiry_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 1024)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;
}