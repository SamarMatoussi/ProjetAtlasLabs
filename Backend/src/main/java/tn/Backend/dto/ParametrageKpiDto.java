package tn.Backend.dto;

import lombok.Builder;
import lombok.Data;
import tn.Backend.entites.Appreciation;
import tn.Backend.entites.ParametrageKpi;

@Data
@Builder
public class ParametrageKpiDto {
    private Long id;
    private String name;
    private String description;
    private Appreciation appreciation;
    private Long kpiId;

    // Champs pour le type NUMERIQUE
    private Long max;
    private Long min;

    // Champs pour le type STRING
    private String utilite;

    public static ParametrageKpiDto fromEntity(ParametrageKpi paramKpi) {
        if (paramKpi == null) {
            return null;
        }

        return ParametrageKpiDto.builder()
                .id(paramKpi.getId())
                .name(paramKpi.getName())
                .description(paramKpi.getDescription())
                .appreciation(paramKpi.getAppreciation())
                .kpiId(paramKpi.getKpi() != null ? paramKpi.getKpi().getId() : null)
                .max(paramKpi.isNumeric() ? paramKpi.getMax() : null) // Ajouter le max si c'est numérique
                .min(paramKpi.isNumeric() ? paramKpi.getMin() : null) // Ajouter le min si c'est numérique
                .utilite(paramKpi.isString() ? paramKpi.getUtilite() : null) // Ajouter la utilite si c'est une chaîne
                .build();
    }

    public static ParametrageKpi toEntity(ParametrageKpiDto dto) {
        if (dto == null) {
            return null;
        }

        ParametrageKpi paramKpi = new ParametrageKpi();
        paramKpi.setId(dto.getId());
        paramKpi.setName(dto.getName());
        paramKpi.setDescription(dto.getDescription());
        paramKpi.setAppreciation(dto.getAppreciation());

        // Assurez-vous que les valeurs max/min/utilite sont définies correctement
        paramKpi.setMax(dto.getMax());
        paramKpi.setMin(dto.getMin());
        paramKpi.setUtilite(dto.getUtilite());

        return paramKpi;
    }
}
