package tn.Backend.services;

import tn.Backend.dto.KpiDto;
import tn.Backend.dto.KpiWithParametrageDto;

import java.util.List;
import java.util.Optional;

public interface KpiService {
    List<KpiDto> getAllKpis();

    List<KpiDto> getAllKpisByActivite(Long activiteId);

    Optional<KpiDto> getKpiById(Long id);

    KpiDto saveOrUpdateKpi(KpiDto kpiDto);

    void deleteKpi(Long id);

    //List<KpiWithParametrageDto> getAllKpiWithParametrage();

    List<KpiWithParametrageDto> getAllKpiWithParametrageByActivite(Long activiteId);

   // List<String> getOptionsByKpiId(Long kpiId);
}
