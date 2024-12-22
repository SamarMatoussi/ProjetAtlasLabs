package tn.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.Backend.entites.Activites;

@Repository
public interface ActivitesRepository extends JpaRepository<Activites, Long> {
}
