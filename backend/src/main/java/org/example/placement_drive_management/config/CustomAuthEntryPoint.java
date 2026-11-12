package org.example.placement_drive_management.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Returns a clean JSON 401 response instead of Spring's default HTML error page.
 * Triggered when an unauthenticated request hits a protected endpoint.
 */
@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String json = """
                {
                  "success": false,
                  "message": "Unauthorized: %s",
                  "timestamp": "%s"
                }
                """.formatted(authException.getMessage(), LocalDateTime.now());

        response.getWriter().write(json);
    }
}