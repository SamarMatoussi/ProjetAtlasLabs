package tn.Backend.services;

import tn.Backend.dto.ActivitesDto;
import tn.Backend.dto.PosteDto;
import tn.Backend.entites.Activites;

import java.util.List;
import java.util.Optional;

public interface ActivitesService {

    List<ActivitesDto> getAllActivites();

    Optional<ActivitesDto> getActivitesById(Long id);

    ActivitesDto saveOrUpdateActivites(ActivitesDto activitesDto);

    void deleteActivites(Long id);

    ActivitesDto addPostesToActivite(Long activiteId, List<PosteDto> postesDto);

    ActivitesDto removePostesFromActivite(Long activiteId, List<PosteDto> postesDto);

    Activites addActivite(ActivitesDto activiteDto);
}
