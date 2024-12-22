package tn.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.Backend.dto.ActivitesDto;
import tn.Backend.dto.PosteDto;
import tn.Backend.exception.ResourceNotFound;
import tn.Backend.services.PosteService;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/postes")
public class PosteController {

    @Autowired
    private PosteService posteService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT')")
    @GetMapping("/lister")
    public ResponseEntity<List<PosteDto>> getAllPostes() {
        List<PosteDto> postes = posteService.getAllPostes();
        return ResponseEntity.ok(postes);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/poste/{id}")
    public ResponseEntity<PosteDto> getPosteById(@PathVariable Long id) throws ResourceNotFound {
        PosteDto poste = posteService.getPosteById(id);
        if (poste != null) {
            return ResponseEntity.ok(poste);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/addposte")
    public ResponseEntity<PosteDto> createPoste(@RequestBody PosteDto posteDto) {
        PosteDto createdPoste = posteService.createPoste(posteDto);
        return new ResponseEntity<>(createdPoste, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/updateposte/{id}")
    public ResponseEntity<PosteDto> updatePoste(@PathVariable Long id, @RequestBody PosteDto posteDto) {
        try {
            PosteDto updatedPoste = posteService.updatePoste(id, posteDto);
            return new ResponseEntity<>(updatedPoste, HttpStatus.OK);
        } catch (ResourceNotFound e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePoste(@PathVariable Long id) {
        posteService.deletePoste(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // Ajouter des activités à un poste
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{posteId}/activites")
    public ResponseEntity<PosteDto> addActivitesToPoste(@PathVariable Long posteId, @RequestBody List<ActivitesDto> activitesDto) throws ResourceNotFound {
        PosteDto updatedPoste = posteService.addActivitesToPoste(posteId, activitesDto);
        if (updatedPoste != null) {
            return ResponseEntity.ok(updatedPoste);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Retirer des activités d'un poste
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{posteId}/activites")
    public ResponseEntity<PosteDto> removeActivitesFromPoste(@PathVariable Long posteId, @RequestBody List<ActivitesDto> activitesDto) throws ResourceNotFound {
        PosteDto updatedPoste = posteService.removeActivitesFromPoste(posteId, activitesDto);
        if (updatedPoste != null) {
            return ResponseEntity.ok(updatedPoste);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
