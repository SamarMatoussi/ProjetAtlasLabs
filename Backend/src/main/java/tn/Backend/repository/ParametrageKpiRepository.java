package tn.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.Backend.entites.Kpi;
import tn.Backend.entites.ParametrageKpi;

import java.util.List;

@Repository
public interface ParametrageKpiRepository extends JpaRepository<ParametrageKpi, Long> {
    List<ParametrageKpi> findAllByKpiId(Long kpiId);

}
