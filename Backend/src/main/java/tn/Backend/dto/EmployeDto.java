package tn.Backend.dto;

import lombok.Getter;
import lombok.Setter;
import tn.Backend.auth.RegisterRequest;
import tn.Backend.entites.Agent;
import tn.Backend.entites.Employe;
import tn.Backend.entites.Poste;

@Getter
@Setter
public class EmployeDto extends RegisterRequest {
    private Long agentId;
    private Long posteId;

    // Mapper de l'entité Employe vers DTO
    public static EmployeDto fromEntity(Employe employe) {
        if (employe == null) {
            return null;
        }

        EmployeDto dto = new EmployeDto();
        dto.setFirstname(employe.getFirstname());
        dto.setLastname(employe.getLastname());
        dto.setCin(employe.getCin());
        dto.setEmail(employe.getEmail());
        dto.setPassword(employe.getPassword());
        dto.setPhone(employe.getPhone());
        dto.setIsEnabled(employe.getIsEnabled());
        dto.setAgentId(employe.getAgent() != null ? employe.getAgent().getId() : null);
        dto.setPosteId(employe.getPoste() != null ? employe.getPoste().getId() : null);

        return dto;
    }

    public static Employe toEntity(EmployeDto dto) {
        if (dto == null) {
            return null;
        }

        // Utilisation du builder pour créer l'entité Employe avec isEnabled par défaut à true
        Employe employe = Employe.builder()
                .cin(dto.getCin())
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .phone(dto.getPhone())
                .isEnabled(true)  // Définit isEnabled à true par défaut
                .build();

        // Associer l'Agent si l'ID est défini
        if (dto.getAgentId() != null) {
            Agent agent = new Agent();
            agent.setId(dto.getAgentId());
            employe.setAgent(agent);
        }

        // Associer le Poste si l'ID est défini
        if (dto.getPosteId() != null) {
            Poste poste = new Poste();
            poste.setId(dto.getPosteId());
            employe.setPoste(poste);
        }

        return employe;
    }
}
