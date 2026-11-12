package org.example.placement_drive_management.dto;

import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.placement_drive_management.entity.Applications;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {
    private  Long id;
    private String companyId;
    private String companyName;
    private String website;
    private String industryType;
    private String description;
    private String externalApplicationLink;

}
