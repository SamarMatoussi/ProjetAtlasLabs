package tn.Backend.dto;

import lombok.Data;

import java.util.List;
@Data
public class KpiWithParametrageDto {

    private KpiDto kpi;
    private List<ParametrageKpiDto> parametrages;
}
