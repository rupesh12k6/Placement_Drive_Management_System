package org.example.placement_drive_management.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.placement_drive_management.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Getter
    @Value("${app.jwt.access-token-expiration}")
    private long accessTokenExpirationMs;

    // ============================
    // Token Generation
    // ============================

    public String generateAccessToken(UserDetails userDetails, Role role) {

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ============================
    // Token Validation
    // ============================

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);

            return username.equals(userDetails.getUsername())
                    && !isTokenExpired(token);

        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    // ============================
    // Claim Extraction
    // ============================

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Role extractRole(String token) {
        String roleStr = extractClaim(token,
                claims -> claims.get("role", String.class));
        return Role.valueOf(roleStr);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token,
                              Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ============================
    // Signing Key
    // ============================

    private SecretKey getSigningKey() {

        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                    "JWT secret key must be at least 32 characters long for HS256"
            );
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ============================
    // Getter
    // ============================

}