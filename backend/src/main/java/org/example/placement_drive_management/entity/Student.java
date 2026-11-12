package org.example.placement_drive_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.placement_drive_management.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="student")
public class Student implements UserDetails {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(unique = true)
    @Size(min = 12, max = 12)
    private String rollNo;
    @NotNull
    @Column(length=15)
    private String name;
    @NotNull
    @Column(length=15)
    private String surname;
    @Size(min=10, max=10)
    private String mobileNo;
    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@anits\\.edu\\.in$",message = "Enter the valid college email")
    @Column(length=100, unique = true)
    private String email;
    @NotNull
    @Column(length=256)
    private String password;
    @OneToOne(mappedBy = "student",cascade = CascadeType.ALL)
    private StudentProfile studentProfile;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_STUDENT;

    // ── UserDetails ──────────────────────────────────────────────

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    /** Spring Security uses getUsername() as the principal identifier */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired()     {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()      {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
