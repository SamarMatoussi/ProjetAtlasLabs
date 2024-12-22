package tn.Backend.dto;

import lombok.Builder;
import lombok.Data;
import tn.Backend.entites.Departement;

@Data
@Builder
public class DepartementDto {
    private String name;

    public static DepartementDto fromEntity(Departement departement) {
        return DepartementDto.builder()
                .name(departement.getName())
                .build();
    }

    public static Departement toEntity(DepartementDto dto) {
        if (dto == null) {
            return null;
        }

        Departement departement = new Departement();
        departement.setName(dto.getName());

        return departement;
    }
}
