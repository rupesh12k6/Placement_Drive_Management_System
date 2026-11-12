package org.example.placement_drive_management.mappers;

import org.example.placement_drive_management.dto.CompanyDto;
import org.example.placement_drive_management.entity.Company;

public class CompanyMapper {
    public static CompanyDto mapCompanyToDto(Company company) {
        return new CompanyDto(
                company.getId(),
                company.getCompanyId(),
                company.getCompanyName(),
                company.getWebsite(),
                company.getIndustryType(),
                company.getDescription(),
                company.getExternalApplicationLink()
        );
    }
}
