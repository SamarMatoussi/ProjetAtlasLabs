package tn.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.Backend.entites.Kpi;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface KpiRepository extends JpaRepository<Kpi, Long> {
    List<Kpi> findAllByActivitesId(Long activiteId);

    Optional<Kpi> findByNameKpi(String nameKpi);
   /* @Query("SELECT o.optionValue FROM KpiOption o WHERE o.kpi.id = :kpiId")
    List<String> findOptionsByKpiId(@Param("kpiId") Long kpiId);*/
}
