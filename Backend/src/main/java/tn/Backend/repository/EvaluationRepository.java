package tn.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.Backend.entites.Evaluation;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    List<Evaluation> findByEmployeId(Long employeId);

    List<Evaluation> findByKpiId(Long kpiId);

    List<Evaluation> findByEmployeIdAndKpiId(Long employeId, Long kpiId);
}
