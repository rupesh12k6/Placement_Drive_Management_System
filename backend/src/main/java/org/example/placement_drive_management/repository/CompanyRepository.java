package org.example.placement_drive_management.repository;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import org.example.placement_drive_management.dto.CompanyDto;
import org.example.placement_drive_management.entity.Company;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPersistentAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByCompanyId(String companyId);
    Optional<Company> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByCompanyId(String companyId);
}
