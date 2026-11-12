package org.example.placement_drive_management.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Returns a clean JSON 403 response when an authenticated user
 * tries to access a resource they don't have permission for.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String json = """
                {
                  "success": false,
                  "message": "Forbidden: You do not have permission to access this resource.",
                  "timestamp": "%s"
                }
                """.formatted(LocalDateTime.now());

        response.getWriter().write(json);
    }
}