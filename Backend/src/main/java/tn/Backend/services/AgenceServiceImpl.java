package tn.Backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.Backend.dto.AgenceDto;
import tn.Backend.entites.Agence;
import tn.Backend.repository.AgenceRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgenceServiceImpl implements AgenceService {

    private final AgenceRepository agenceRepository;


    public List<AgenceDto> getAllAgences() {
        List<Agence> agences = agenceRepository.findAll();
        return agences.stream()
                .map(AgenceDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<AgenceDto> getAgenceById(Long id) {
        return agenceRepository.findById(id)
                .map(AgenceDto::fromEntity);
    }

    public AgenceDto saveOrUpdateAgence(AgenceDto agenceDto) {
        Agence agence = AgenceDto.toEntity(agenceDto);
        Agence savedAgence = agenceRepository.save(agence);
        return AgenceDto.fromEntity(savedAgence);
    }

    public void deleteAgence(Long id) {
        agenceRepository.deleteById(id);
    }
}
