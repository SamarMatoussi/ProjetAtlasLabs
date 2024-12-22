package tn.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.Backend.entites.Pointage;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PointageRepository extends JpaRepository<Pointage, Long> {

    List<Pointage> findByDate(LocalDateTime date);
    List<Pointage> findByCinOrderByDateAsc(Long cin);


}
