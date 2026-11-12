package org.example.placement_drive_management.config;
import org.springframework.beans.factory.annotation.Value;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.example.placement_drive_management.entity.Admin;
import org.example.placement_drive_management.enums.Role;
import org.example.placement_drive_management.repository.AdminRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j

public class SuperAdminSeeder implements ApplicationRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${superadmin.email}")
    private String email;

    @Value("${superadmin.password}")
    private String password;

    @Value("${superadmin.name}")
    private String name;

    @Override
    public void run(ApplicationArguments args) {
        if (!adminRepository.existsByRole(Role.ROLE_SUPER_ADMIN)) {
            Admin superAdmin = new Admin();
            superAdmin.setName(name);
            superAdmin.setEmail(email);
            superAdmin.setPassword(passwordEncoder.encode(password));
            superAdmin.setRole(Role.ROLE_SUPER_ADMIN);
            adminRepository.save(superAdmin);
            log.info("SUPER_ADMIN seeded: {}", email);
        } else {
            log.info("SUPER_ADMIN already exists. Skipping.");
        }
    }
}
