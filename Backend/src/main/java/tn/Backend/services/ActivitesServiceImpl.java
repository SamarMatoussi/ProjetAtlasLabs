package tn.Backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.Backend.dto.ActivitesDto;
import tn.Backend.dto.PosteDto;
import tn.Backend.entites.Activites;
import tn.Backend.entites.Poste;
import tn.Backend.repository.ActivitesRepository;
import tn.Backend.repository.PosteRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivitesServiceImpl implements ActivitesService {

    private final ActivitesRepository activitesRepository;
    private final PosteRepository posteRepository;

    public List<ActivitesDto> getAllActivites() {
        List<Activites> activites = activitesRepository.findAll();
        return activites.stream()
                .map(ActivitesDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<ActivitesDto> getActivitesById(Long id) {
        return activitesRepository.findById(id)
                .map(ActivitesDto::fromEntity);
    }

    public ActivitesDto saveOrUpdateActivites(ActivitesDto activitesDto) {
        Activites activites = ActivitesDto.toEntity(activitesDto);
        Activites savedActivites = activitesRepository.save(activites);
        return ActivitesDto.fromEntity(savedActivites);
    }

    public void deleteActivites(Long id) {
        activitesRepository.deleteById(id);
    }

    // Méthode pour ajouter des postes à une activité
    public ActivitesDto addPostesToActivite(Long activiteId, List<PosteDto> postesDto) {
        Activites activites = activitesRepository.findById(activiteId)
                .orElseThrow(() -> new RuntimeException("Activite not found with id " + activiteId));

        List<Poste> postes = postesDto.stream()
                .map(PosteDto::toEntity)
                .collect(Collectors.toList());

        // Vérifiez l'existence des postes avant de les ajouter
       /* for (Poste poste : postes) {
            Poste existingPoste = posteRepository.findById(poste.getId())
                    .orElseThrow(() -> new RuntimeException("Poste not found with id " + poste.getId()));
            activites.getPostes().add(existingPoste); // Ajoutez uniquement les postes existants
        }*/

        Activites updatedActivites = activitesRepository.save(activites);
        return ActivitesDto.fromEntity(updatedActivites);
    }

    // Méthode pour retirer des postes d'une activité
    public ActivitesDto removePostesFromActivite(Long activiteId, List<PosteDto> postesDto) {
        Activites activites = activitesRepository.findById(activiteId)
                .orElseThrow(() -> new RuntimeException("Activite not found with id " + activiteId));

        List<Poste> postes = postesDto.stream()
                .map(PosteDto::toEntity)
                .collect(Collectors.toList());

        // Vérifiez l'existence des postes avant de les retirer
       /* for (Poste poste : postes) {
            Poste existingPoste = posteRepository.findById(poste.getId())
                    .orElseThrow(() -> new RuntimeException("Poste not found with id " + poste.getId()));
            activites.getPostes().remove(existingPoste); // Retirer uniquement les postes existants
        }*/

        Activites updatedActivites = activitesRepository.save(activites);
        return ActivitesDto.fromEntity(updatedActivites);
    }


    public Activites addActivite(ActivitesDto activiteDto) {
        Activites activite = new Activites();
        activite.setName(activiteDto.getName());
        activite.setDescription(activiteDto.getDescription());

        // Charger les postes depuis la base de données
        List<Poste> postes = posteRepository.findAllById(activiteDto.getPosteIds());
        //activite.setPostes(postes);

        // Sauvegarder l'activite avec la relation
        return activitesRepository.save(activite);
    }

}
