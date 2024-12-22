package tn.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.Backend.entites.Departement;
import tn.Backend.services.DepartementService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departements")
@PreAuthorize("hasRole('ADMIN')")
public class DepartementController {
    @Autowired
    private DepartementService departementService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public List<Departement> getAllDepartements() {
        return departementService.getAllDepartements();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public Departement getDepartementById(@PathVariable Long id) {
        return departementService.getDepartementById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Departement not found with id " + id));
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public Departement createDepartement(@RequestBody Departement departement) {
        return departementService.saveOrUpdateDepartement(departement);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public Departement updateDepartement(@PathVariable Long id, @RequestBody Departement departement) {
        departement.setId(id);
        return departementService.saveOrUpdateDepartement(departement);
    }

    @DeleteMapping("/{id}")
    public void deleteDepartement(@PathVariable Long id) {
        departementService.deleteDepartement(id);
    }
}
