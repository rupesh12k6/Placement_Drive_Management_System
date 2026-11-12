package org.example.placement_drive_management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.placement_drive_management.repository.AdminRepository;
import org.example.placement_drive_management.repository.CompanyRepository;
import org.example.placement_drive_management.repository.StudentRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Resolves a UserDetails by email.
 * Lookup order: Student → Admin.
 * Both entities implement UserDetails directly, so no conversion needed.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;
    private final CompanyRepository companyRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. Try Student table
        var student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            log.debug("Found user in Student table: {}", email);
            return student.get();
        }

        // 2. Try Admin table
        var admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            log.debug("Found user in Admin table: {}", email);
            return admin.get();
        }
        // 3. Try Company table
        var company = companyRepository.findByEmail(email);
        if (company.isPresent()) {
            log.debug("Found user in Company table: {}", email);
            return company.get();
        }
        log.warn("No user found with email: {}", email);
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}