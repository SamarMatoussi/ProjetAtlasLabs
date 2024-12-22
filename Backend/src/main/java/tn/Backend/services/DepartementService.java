package tn.Backend.services;


import tn.Backend.entites.Departement;

import java.util.List;
import java.util.Optional;

public interface DepartementService {
    List<Departement> getAllDepartements();

    Optional<Departement> getDepartementById(Long id);

    Departement saveOrUpdateDepartement(Departement departement);

    void deleteDepartement(Long id);

}
