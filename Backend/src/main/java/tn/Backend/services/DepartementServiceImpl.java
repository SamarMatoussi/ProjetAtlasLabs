package tn.Backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.Backend.entites.Departement;
import tn.Backend.repository.DepartementRepository;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartementServiceImpl implements DepartementService{


  private final DepartementRepository departementRepository;


    public List<Departement> getAllDepartements() {
        return departementRepository.findAll();
    }

    public Optional<Departement> getDepartementById(Long id) {
        return departementRepository.findById(id);
    }

    public Departement saveOrUpdateDepartement(Departement departement) {
        return departementRepository.save(departement);
    }

    public void deleteDepartement(Long id) {
        departementRepository.deleteById(id);
    }
}
