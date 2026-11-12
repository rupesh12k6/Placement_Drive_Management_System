package org.example.placement_drive_management.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.placement_drive_management.enums.Role;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;

    /** Always "Bearer" */
    private String tokenType;

    /** Access token TTL in seconds (900 = 15 min) */
    private long accessTokenExpiresIn;

    private String email;
    private Role role;
}