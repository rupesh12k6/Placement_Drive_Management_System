// ============================================================
// FILE: dto/auth/LoginRequest.java
// ============================================================
package org.example.placement_drive_management.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    /** Optional — used to support multi-device session tracking */
    private String deviceInfo;
}