package tn.Backend.dto;

import lombok.Builder;
import lombok.Data;
import tn.Backend.entites.Activites;
import tn.Backend.entites.Poste;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ActivitesDto {
    private Long id;
    private String name;
    private String description;
    private List<Long> posteIds; // Ajout d'une liste d'IDs de postes

    public static ActivitesDto fromEntity(Activites activites) {
        if (activites == null) {
            return null;
        }
        return ActivitesDto.builder()
                .id(activites.getId())
                .name(activites.getName())
                .description(activites.getDescription())
                /*.posteIds(activites.getPostes() != null ?
                        activites.getPostes().stream()
                                .map(Poste::getId) // Récupération des IDs des postes
                                .collect(Collectors.toList()) : null)*/
                .build();
    }

    public static Activites toEntity(ActivitesDto dto) {
        if (dto == null) {
            return null;
        }

        Activites activites = new Activites();
        activites.setId(dto.getId());
        activites.setName(dto.getName());
        activites.setDescription(dto.getDescription());
        return activites;
    }
}
