package tn.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.Backend.entites.Agence;

@Repository
public interface AgenceRepository extends JpaRepository<Agence, Long> {
}

