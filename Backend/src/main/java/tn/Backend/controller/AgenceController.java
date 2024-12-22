package tn.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.Backend.dto.AgenceDto;
import tn.Backend.services.AgenceService;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/agence")
public class AgenceController {
    @Autowired
    private AgenceService agenceService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("lister")
    public List<AgenceDto> getAllAgences() {
        return agenceService.getAllAgences();
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/agence/{id}")
    public AgenceDto getAgenceById(@PathVariable Long id) {
        return agenceService.getAgenceById(id)
                .orElseThrow(() -> new RuntimeException("Agence not found with id " + id));
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/addagence")
    public AgenceDto createAgence(@RequestBody AgenceDto agenceDto) {
        return agenceService.saveOrUpdateAgence(agenceDto);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/updateagence/{id}")
    public AgenceDto updateAgence(@PathVariable Long id, @RequestBody AgenceDto agenceDto) {
        agenceDto.setId(id);
        return agenceService.saveOrUpdateAgence(agenceDto);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/deleteagence/{id}")
    public void deleteAgence(@PathVariable Long id) {
        agenceService.deleteAgence(id);
    }
}

