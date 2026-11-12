package org.example.placement_drive_management.dto.auth;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class RegisterStudentRequest {
    @NotBlank(message = "Roll number is required")
    @Size(min = 12, max = 12, message = "Roll number must be exactly 12 characters")
    private String rollNo;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Surname is required")
    private String surname;

    @NotBlank(message = "Email is required")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@anits\\.edu\\.in$",
            message = "Enter a valid college email (@anits.edu.in)"
            
    )
    private String email;
    @Size(min = 10, max = 10)
    private String mobileNo;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    private String deviceInfo;
}