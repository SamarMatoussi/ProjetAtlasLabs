package tn.Backend.dto;

import lombok.Builder;
import lombok.Data;
import tn.Backend.entites.Agence;

@Data
@Builder
public class AgenceDto {
    private Long id;
    private String nom;
    private String adresse;
    private String numeroTelephone;
    private String codePostal;
    private String email;

    public static AgenceDto fromEntity(Agence agence) {
        return AgenceDto.builder()
                .id(agence.getId())
                .nom(agence.getNom())
                .adresse(agence.getAdresse())
                .numeroTelephone(agence.getNumeroTelephone())
                .codePostal(agence.getCodePostal())
                .email(agence.getEmail())
                .build();
    }

    public static Agence toEntity(AgenceDto dto) {
        if (dto == null) {
            return null;
        }

        Agence agence = new Agence();
        agence.setId(dto.getId());
        agence.setNom(dto.getNom());
        agence.setAdresse(dto.getAdresse());
        agence.setNumeroTelephone(dto.getNumeroTelephone());
        agence.setCodePostal(dto.getCodePostal());
        agence.setEmail(dto.getEmail());

        return agence;
    }
}
