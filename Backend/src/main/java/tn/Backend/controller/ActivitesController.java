package tn.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.Backend.dto.ActivitesDto;
import tn.Backend.dto.PosteDto; // Importation du DTO Poste
import tn.Backend.entites.Activites;
import tn.Backend.services.ActivitesService;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/activite")
public class ActivitesController {

    @Autowired
    private ActivitesService activitesService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT')")
    @GetMapping("/liste")
    public ResponseEntity<List<ActivitesDto>> getAllActivites() {
        List<ActivitesDto> activites = activitesService.getAllActivites();
        return ResponseEntity.ok(activites);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/activite/{id}")
    public ResponseEntity<ActivitesDto> getActivitesById(@PathVariable Long id) {
        return activitesService.getActivitesById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /*@PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/addactivite")
    public ResponseEntity<ActivitesDto> createActivites(@RequestBody ActivitesDto activitesDto) {
        ActivitesDto createdActivite = activitesService.saveOrUpdateActivites(activitesDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdActivite);
    }*/
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/addactivite")
    public ResponseEntity<Activites> addActivite(@RequestBody ActivitesDto activiteDto) {
        try {
            // Appeler le service pour ajouter une activité
            Activites activite = activitesService.addActivite(activiteDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(activite);
        } catch (Exception e) {
            // Gérer les exceptions et retourner une réponse adéquate
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/updateactivite/{id}")
    public ResponseEntity<ActivitesDto> updateActivites(@PathVariable Long id, @RequestBody ActivitesDto activitesDto) {
        activitesDto.setId(id);
        ActivitesDto updatedActivite = activitesService.saveOrUpdateActivites(activitesDto);
        return ResponseEntity.ok(updatedActivite);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteActivites(@PathVariable Long id) {
        activitesService.deleteActivites(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{activiteId}/postes")
    public ResponseEntity<ActivitesDto> addPostesToActivite(@PathVariable Long activiteId, @RequestBody List<PosteDto> postesDto) {
        ActivitesDto updatedActivite = activitesService.addPostesToActivite(activiteId, postesDto);
        return ResponseEntity.ok(updatedActivite);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{activiteId}/postes")
    public ResponseEntity<ActivitesDto> removePostesFromActivite(@PathVariable Long activiteId, @RequestBody List<PosteDto> postesDto) {
        ActivitesDto updatedActivite = activitesService.removePostesFromActivite(activiteId, postesDto);
        return ResponseEntity.ok(updatedActivite);
    }
}
