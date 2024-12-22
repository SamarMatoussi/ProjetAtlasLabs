package tn.Backend.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import tn.Backend.entites.Activites;
import tn.Backend.entites.Poste;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Builder
public class PosteDto {
    private Long id;
    private String name;
    private String description;
    private List<Long> activiteIds; // Ajout d'une liste d'IDs d'activités

    public static PosteDto fromEntity(Poste poste) {
        if (poste == null) {
            return null;
        }

        return PosteDto.builder()
                .id(poste.getId())
                .name(poste.getName())
                .description(poste.getDescription())
               /* .activiteIds(poste.getActivites() != null ?
                        poste.getActivites().stream()
                                .map(Activites::getId) // Récupération des IDs des activités
                                .collect(Collectors.toList()) : null)*/
                .build();
    }

    public static Poste toEntity(PosteDto posteDto) {
        if (posteDto == null) {
            return null;
        }

        Poste poste = new Poste();
        poste.setId(posteDto.getId());
        poste.setName(posteDto.getName());
        poste.setDescription(posteDto.getDescription());
        return poste;
    }


}
