package org.example.placement_drive_management.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.placement_drive_management.dto.auth.*;
import org.example.placement_drive_management.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * All endpoints under /api/auth/** are PUBLIC (no token required).
 *
 * Token transport: Authorization: Bearer <accessToken>
 *
 * Client flow:
 *   1. POST /register or /login  → receive { accessToken, refreshToken }
 *   2. Include accessToken in every request: Authorization: Bearer <token>
 *   3. When 401 received → POST /refresh with refreshToken → get new accessToken
 *   4. POST /logout to invalidate both tokens
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ── POST /api/auth/register/student ──────────────────────────

    @PostMapping("/register/student")
    public ResponseEntity<ApiResponse<AuthResponse>> registerStudent(
            @Valid @RequestBody RegisterStudentRequest request) {

        AuthResponse response = authService.registerStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student registered successfully", response));
    }

    // ── POST /api/auth/register/admin ────────────────────────

    @PostMapping("/register/admin")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AuthResponse>> registerAdmin(
            @Valid @RequestBody RegisterAdminRequest request) {

        AuthResponse response = authService.registerAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Admin registered successfully", response));
    }
    // ── POST /api/auth/login ──────────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
    @PostMapping("/register/company")
    public ResponseEntity<ApiResponse<AuthResponse>> registerCompany(
            @Valid @RequestBody RegisterCompanyRequest request) {

        AuthResponse response = authService.registerCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Company registered successfully", response));
    }

    // ── POST /api/auth/refresh ────────────────────────────────────
    /**
     * Call this when the access token has expired (you received a 401).
     * Returns a new access token AND a rotated refresh token.
     * Store the new refresh token — the old one is now invalid.
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    // ── POST /api/auth/logout ─────────────────────────────────────
    /**
     * Requires: Authorization: Bearer <accessToken> header
     *           + { "refreshToken": "..." } in the request body.
     *
     * After calling this:
     *   - The refresh token is revoked in the DB.
     *   - The access token is blacklisted until it naturally expires.
     *   - Client must delete both tokens from local storage.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody LogoutRequest request,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {

        String rawToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            rawToken = authHeader.substring(7);
        }

        authService.logout(request, rawToken);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }
}