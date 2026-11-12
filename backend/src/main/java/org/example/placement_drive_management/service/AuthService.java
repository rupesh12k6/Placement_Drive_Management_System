package org.example.placement_drive_management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.placement_drive_management.dto.auth.*;
import org.example.placement_drive_management.entity.Admin;
import org.example.placement_drive_management.entity.Company;
import org.example.placement_drive_management.entity.RefreshToken;
import org.example.placement_drive_management.entity.Student;
import org.example.placement_drive_management.enums.Role;
import org.example.placement_drive_management.exceptions.DuplicateEmailException;
import org.example.placement_drive_management.repository.AdminRepository;
import org.example.placement_drive_management.repository.CompanyRepository;
import org.example.placement_drive_management.repository.StudentRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private final CustomUserDetailsService userDetailsService;
    private final CompanyRepository companyRepository;

    @Transactional
    public AuthResponse registerStudent(RegisterStudentRequest request) {
        checkEmailNotTaken(request.getEmail());
        if (studentRepository.existsByRollNo(request.getRollNo())) {
            throw new IllegalArgumentException("Roll number is already registered: " + request.getRollNo());
        }

        Student student = new Student();
        student.setRollNo(request.getRollNo());
        student.setName(request.getName());
        student.setSurname(request.getSurname());
        student.setEmail(request.getEmail());
        student.setMobileNo(request.getMobileNo());
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setRole(Role.ROLE_STUDENT);

        studentRepository.save(student);
        log.info("Registered new student: {}", request.getEmail());

        return buildAuthResponse(student, Role.ROLE_STUDENT, request.getDeviceInfo());
    }

    @Transactional
    public AuthResponse registerAdmin(RegisterAdminRequest request) {
        checkEmailNotTaken(request.getEmail());

        Admin admin = new Admin();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setRole(Role.ROLE_ADMIN);

        adminRepository.save(admin);
        log.info("Registered new admin: {}", request.getEmail());

        return buildAuthResponse(admin, Role.ROLE_ADMIN, request.getDeviceInfo());
    }

    @Transactional
    public AuthResponse registerCompany(RegisterCompanyRequest request) {
        checkEmailNotTaken(request.getEmail());
        if (companyRepository.existsByCompanyId(request.getCompanyId())) {
            throw new IllegalArgumentException("Company ID is already registered: " + request.getCompanyId());
        }

        Company company = new Company();
        company.setCompanyId(request.getCompanyId());
        company.setCompanyName(request.getCompanyName());
        company.setEmail(request.getEmail());
        company.setPassword(passwordEncoder.encode(request.getPassword()));
        company.setWebsite(request.getWebsite());
        company.setIndustryType(request.getIndustryType());
        company.setDescription(request.getDescription());
        company.setExternalApplicationLink(request.getExternalApplicationLink());
        company.setRole(Role.ROLE_COMPANY);

        companyRepository.save(company);
        log.info("Registered new company: {}", request.getEmail());

        return buildAuthResponse(company, Role.ROLE_COMPANY, request.getDeviceInfo());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Role role = resolveRole(userDetails);

        log.info("User logged in: {} [{}]", request.getEmail(), role);
        return buildAuthResponse(userDetails, role, request.getDeviceInfo());
    }

    // Consolidated login methods (Issue 10)
    @Transactional public AuthResponse companyLogin(LoginRequest request) { return login(request); }
    @Transactional public AuthResponse adminLogin(LoginRequest request) { return login(request); }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken oldToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(oldToken.getEmail());
        Role currentRole = resolveRole(userDetails);
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(oldToken);
        String newAccessToken = jwtService.generateAccessToken(userDetails, currentRole);

        log.info("Tokens rotated for: {}", oldToken.getEmail());
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .accessTokenExpiresIn(jwtService.getAccessTokenExpirationMs() / 1000)
                .email(userDetails.getUsername())
                .role(currentRole)
                .build();
    }

    @Transactional
    public void logout(LogoutRequest request, String rawAccessToken) {
        refreshTokenService.revokeToken(request.getRefreshToken());
        if (rawAccessToken != null && !rawAccessToken.isBlank()) {
            try {
                Date expiry = jwtService.extractExpiration(rawAccessToken);
                tokenBlacklistService.blacklist(rawAccessToken, expiry.toInstant());
            } catch (Exception e) {
                log.debug("Could not blacklist access token on logout: {}", e.getMessage());
            }
        }
        log.info("User logged out successfully.");
    }

    private AuthResponse buildAuthResponse(UserDetails userDetails, Role role, String deviceInfo) {
        String accessToken  = jwtService.generateAccessToken(userDetails, role);
        RefreshToken rt     = refreshTokenService.createRefreshToken(
                userDetails.getUsername(), role.name(), deviceInfo);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rt.getToken())
                .tokenType("Bearer")
                .accessTokenExpiresIn(jwtService.getAccessTokenExpirationMs() / 1000)
                .email(userDetails.getUsername())
                .role(role)
                .build();
    }

    private Role resolveRole(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> Role.valueOf(a.getAuthority()))
                .orElseThrow(() -> new IllegalStateException("User has no role assigned."));
    }

    private void checkEmailNotTaken(String email) {
        // Issue 11: Consolidation of existence checks
        boolean taken = studentRepository.existsByEmail(email)
                || adminRepository.existsByEmail(email)
                || companyRepository.existsByEmail(email);
        if (taken) {
            throw new DuplicateEmailException("Email is already registered: " + email);
        }
    }
}
