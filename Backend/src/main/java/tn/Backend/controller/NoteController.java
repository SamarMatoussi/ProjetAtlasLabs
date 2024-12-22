package tn.Backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.Backend.dto.NoteDto;
import tn.Backend.dto.NoteGlobaleDto;
import tn.Backend.exception.ResourceNotFound;
import tn.Backend.services.NoteService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping("liste")
    @PreAuthorize("hasAnyAuthority('AGENT', 'ADMIN' ,'EMPLOYE')")
    public ResponseEntity<List<NoteDto>> getAllNotes() {
        List<NoteDto> allNotes = noteService.getAllNotes();
        return ResponseEntity.ok(allNotes);
    }
    @PreAuthorize("hasAuthority('AGENT')")
    @GetMapping("/globale/authenticated")
    public List<NoteGlobaleDto> getNoteGlobaleByAuthenticatedAgent() {
        return noteService.getNoteGlobaleByAuthenticatedAgent();
    }

    @PreAuthorize("hasAnyAuthority('AGENT', 'ADMIN')")
    @PostMapping("/addNote")
    public ResponseEntity<NoteDto> ajouterNote(@Valid @RequestBody NoteDto noteDto , Authentication authentication) throws ResourceNotFound {
        NoteDto createdNote = noteService.ajouterNote(noteDto , authentication);
        return ResponseEntity.ok(createdNote);
    }

    @GetMapping("/getNote/{employeCin}/{kpiId}")
    @PreAuthorize("hasAnyAuthority('AGENT', 'ADMIN')")
    public ResponseEntity<NoteDto> getNote(@PathVariable Long employeCin, @PathVariable Long kpiId, Authentication authentication) throws ResourceNotFound {
        NoteDto createdNote = noteService.getNote(employeCin, kpiId, authentication);
        return ResponseEntity.ok(createdNote);
    }


    /*@GetMapping("/employe/{cin}")
    @PreAuthorize("hasAnyAuthority('AGENT', 'ADMIN','EMPLOYE')")
    public ResponseEntity<List<NoteDto>> obtenirNotesParEmployeCin(@PathVariable Long cin) {
        List<NoteDto> notes = noteService.obtenirNotesParEmployeCin(cin);
        return ResponseEntity.ok(notes);
    }*/
    @GetMapping("/employe")
    public ResponseEntity<List<NoteDto>> obtenirNotesParEmployeAuthentifie() {
        try {
            List<NoteDto> notes = noteService.obtenirNotesParEmployeAuthentifie();
            return ResponseEntity.ok(notes); // Retourne la liste des notes avec un code HTTP 200
        } catch (RuntimeException e) {
            // En cas d'erreur (par exemple si l'utilisateur n'est pas authentifié ou n'est pas un employé)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Retourne un code HTTP 403
        }
    }

    @PutMapping("/updateNote/{id}")
    @PreAuthorize("hasAnyAuthority('AGENT', 'ADMIN')")
    public ResponseEntity<NoteDto> mettreAJourNote(@PathVariable Long id, @RequestBody NoteDto noteDto) throws ResourceNotFound {
        NoteDto updatedNote = noteService.mettreAJourNote(id, noteDto);
        return ResponseEntity.ok(updatedNote);
    }
    @PutMapping("/updateNoteByCin/{cin}")
    @PreAuthorize("hasAnyAuthority('AGENT', 'ADMIN')")
    public ResponseEntity<NoteDto> updateNoteByCin(@PathVariable Long cin, @RequestBody NoteDto noteDto) throws ResourceNotFound {
        NoteDto updatedNote = noteService.mettreAJourNoteByCin(cin, noteDto);
        return ResponseEntity.ok(updatedNote);
    }
    @DeleteMapping("/deleteByCin/{cin}")
    @PreAuthorize("hasAnyAuthority('AGENT', 'ADMIN')")
    public ResponseEntity<Void> deleteNoteByCin(@PathVariable Long cin) throws ResourceNotFound {
        noteService.supprimerNoteByCin(cin);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('AGENT', 'ADMIN')")
    public ResponseEntity<Void> supprimerNote(@PathVariable Long id) throws ResourceNotFound {
        noteService.supprimerNote(id);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasAnyAuthority('AGENT', 'ADMIN')")
    @GetMapping("/exportExcel")
    public ResponseEntity<Resource> exportExcel() throws IOException, ResourceNotFound {

        Resource resource = noteService.exportExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Evaluation list.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }
}