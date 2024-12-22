package tn.Backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.Backend.dto.PosteDto;
import tn.Backend.dto.ActivitesDto;
import tn.Backend.entites.Poste;
import tn.Backend.entites.Activites;
import tn.Backend.exception.ResourceNotFound;
import tn.Backend.repository.PosteRepository;
import tn.Backend.repository.ActivitesRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PosteServiceImpl implements PosteService {

    private final PosteRepository posteRepository;
    private final ActivitesRepository activitesRepository;

    @Override
    public List<PosteDto> getAllPostes() {
        return posteRepository.findAll()
                .stream()
                .map(PosteDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PosteDto getPosteById(Long id) throws ResourceNotFound {
        return posteRepository.findById(id)
                .map(PosteDto::fromEntity)
                .orElseThrow(() -> new ResourceNotFound("Poste not found with id: " + id));
    }

    @Override
    public PosteDto createPoste(PosteDto posteDto) {
        Poste poste = PosteDto.toEntity(posteDto);

        // Vérifier que les activités associées existent
     /*   if (posteDto.getActiviteIds() != null && !posteDto.getActiviteIds().isEmpty()) {
            List<Activites> activites = posteDto.getActiviteIds().stream()
                    .map(activiteId -> {
                        try {
                            return activitesRepository.findById(activiteId)
                                    .orElseThrow(() -> new ResourceNotFound("Activité non trouvée avec l'id: " + activiteId));
                        } catch (ResourceNotFound e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
            poste.setActivites(activites); // Associer les activités au poste
        }*/

        Poste savedPoste = posteRepository.save(poste);
        return PosteDto.fromEntity(savedPoste);
    }

@Override
public PosteDto updatePoste(Long id, PosteDto posteDto) throws ResourceNotFound {
        Poste existingPoste = posteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Poste non trouvé avec l'id: " + id));

        existingPoste.setName(posteDto.getName());
        existingPoste.setDescription(posteDto.getDescription());

        // Vérifier que les activités associées existent
        if (posteDto.getActiviteIds() != null && !posteDto.getActiviteIds().isEmpty()) {
            List<Activites> activites = posteDto.getActiviteIds().stream()
                    .map(activiteId -> {
                        try {
                            return activitesRepository.findById(activiteId)
                                    .orElseThrow(() -> new ResourceNotFound("Activité non trouvée avec l'id: " + activiteId));
                        } catch (ResourceNotFound e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
           // existingPoste.setActivites(activites); // Mettre à jour les activités associées
        }

        Poste updatedPoste = posteRepository.save(existingPoste);
        return PosteDto.fromEntity(updatedPoste);
    }


    @Override
    public PosteDto removeActivitesFromPoste(Long posteId, List<ActivitesDto> activitesDto) throws ResourceNotFound {
        Poste poste = posteRepository.findById(posteId)
                .orElseThrow(() -> new ResourceNotFound("Poste not found with id: " + posteId));

        List<Activites> activitesToRemove = activitesDto.stream()
                .map(ActivitesDto::toEntity)
                .collect(Collectors.toList());

        //poste.getActivites().removeAll(activitesToRemove); // Retirer les activités du poste
        Poste updatedPoste = posteRepository.save(poste);
        return PosteDto.fromEntity(updatedPoste);
    }

    @Override
    public PosteDto addActivitesToPoste(Long posteId, List<ActivitesDto> activitesDto) throws ResourceNotFound {
        Poste poste = posteRepository.findById(posteId)
                .orElseThrow(() -> new ResourceNotFound("Poste not found with id: " + posteId));

        List<Activites> activitesToAdd = activitesDto.stream()
                .map(ActivitesDto::toEntity)
                .collect(Collectors.toList());

        // Ajouter les activités si elles n'existent pas déjà
        /*for (Activites activite : activitesToAdd) {
            if (!poste.getActivites().contains(activite)) {
                poste.getActivites().add(activite); // Ajouter l'activité au poste
            }
        }*/

        Poste updatedPoste = posteRepository.save(poste);
        return PosteDto.fromEntity(updatedPoste);
    }

    @Override
    public void deletePoste(Long id) {
        posteRepository.deleteById(id);
    }
}
