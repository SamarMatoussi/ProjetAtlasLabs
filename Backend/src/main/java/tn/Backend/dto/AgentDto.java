package tn.Backend.dto;

import lombok.Getter;
import lombok.Setter;
import tn.Backend.auth.RegisterRequest;
import tn.Backend.entites.Agence;
import tn.Backend.entites.Agent;
import tn.Backend.entites.Employe;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class AgentDto extends RegisterRequest {
    private Long agenceId;
    private List<Long> employeIds;

    public static Agent toEntity(AgentDto agentDto) {
        Agent agent = Agent.builder()
                .firstname(agentDto.getFirstname())
                .lastname(agentDto.getLastname())
                .cin(agentDto.getCin())
                .email(agentDto.getEmail())
                .phone(agentDto.getPhone())
                .password(agentDto.getPassword())
                .role(agentDto.getRole())
                .isEnabled(true)
                .build();

        // Associer l'agence si elle existe
        if (agentDto.getAgenceId() != null) {
            Agence agence = new Agence();
            agence.setId(agentDto.getAgenceId());
            agent.setAgence(agence); // Associez l'agence à l'agent
        }


        return agent;
    }

    public static AgentDto fromEntity(Agent agent) {
        if (agent == null) {
            return null;
        }

        AgentDto agentDto = new AgentDto();
        agentDto.setFirstname(agent.getFirstname());
        agentDto.setLastname(agent.getLastname());
        agentDto.setCin(agent.getCin());
        agentDto.setEmail(agent.getEmail());
        agentDto.setRole(agent.getRole());
        agentDto.setPhone(agent.getPhone());

        if (agent.getAgence() != null) {
            agentDto.setAgenceId(agent.getAgence().getId());
        }
        if (agent.getEmployes() != null) {
            agentDto.setEmployeIds(agent.getEmployes().stream()
                    .map(Employe::getId)
                    .collect(Collectors.toList()));
        }
        return agentDto;
    }
}
