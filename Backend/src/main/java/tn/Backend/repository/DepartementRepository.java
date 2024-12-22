package tn.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.Backend.entites.Departement;

@Repository
public interface DepartementRepository extends JpaRepository<Departement,Long> {
}
