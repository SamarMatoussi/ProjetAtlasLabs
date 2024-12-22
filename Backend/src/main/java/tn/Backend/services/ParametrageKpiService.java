package tn.Backend.services;

import tn.Backend.dto.ParametrageKpiDto;

import java.util.List;
import java.util.Optional;

public interface ParametrageKpiService {


    List<ParametrageKpiDto> getAllParametrageKpisByKpi(Long kpiId);

    Optional<ParametrageKpiDto> getParametrageKpiById(Long id);

    ParametrageKpiDto saveOrUpdateParametrageKpi(ParametrageKpiDto parametrageKpiDto);

    void deleteParametrageKpi(Long id);

    String getAppreciationByKpiIdAndNote(Long kpiId , double note );

    String getAppreciationByKpiIdAndTexte(Long kpiId , String texte );
}

