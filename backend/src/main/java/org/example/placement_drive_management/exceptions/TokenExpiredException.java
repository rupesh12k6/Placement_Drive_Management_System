// ============================================================
// FILE: exception/TokenExpiredException.java
// ============================================================
package org.example.placement_drive_management.exceptions;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}