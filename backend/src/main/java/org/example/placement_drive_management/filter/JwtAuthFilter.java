package org.example.placement_drive_management.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.placement_drive_management.service.CustomUserDetailsService;
import org.example.placement_drive_management.service.JwtService;
import org.example.placement_drive_management.service.TokenBlacklistService;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Intercepts every request and validates the JWT from the Authorization header.
 *
 * Token transport: Authorization: Bearer <accessToken>
 *
 * Steps:
 *  1. Extract Bearer token from header
 *  2. Check token blacklist (post-logout invalidation)
 *  3. Extract email from token
 *  4. Load UserDetails and validate token
 *  5. Set Authentication in SecurityContext
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 1. No Bearer token → skip this filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            // 2. Reject blacklisted tokens (user already logged out)
            if (tokenBlacklistService.isBlacklisted(jwt)) {
                log.warn("Request with blacklisted token rejected: {}", request.getRequestURI());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "Token has been invalidated. Please log in again.");
                return;
            }

            // 3. Extract email (subject)
            final String email = jwtService.extractUsername(jwt);

            // 4. Only authenticate if not already set in context
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 5. Set into SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Authenticated: {} → {}", email, request.getRequestURI());
                }
            }

        } catch (ExpiredJwtException e) {
            // Let the 401 flow naturally — client should call /api/auth/refresh
            log.info("Expired access token for: {}", request.getRequestURI());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}