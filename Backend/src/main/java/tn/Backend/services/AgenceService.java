package tn.Backend.services;

import tn.Backend.dto.AgenceDto;

import java.util.List;
import java.util.Optional;

public interface AgenceService {

    List<AgenceDto> getAllAgences();

    Optional<AgenceDto> getAgenceById(Long id);

    AgenceDto saveOrUpdateAgence(AgenceDto agenceDto);

    void deleteAgence(Long id);
}
