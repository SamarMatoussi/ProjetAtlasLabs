package tn.Backend.dto;

import lombok.Builder;
import lombok.Data;
import tn.Backend.entites.Kpi;
import tn.Backend.entites.KpiType;

@Data
@Builder
public class KpiDto {
    private Long id;
    private String nameKpi;
    private String label;
    private String description;
    private KpiType type;
    private Long activiteId;

    public static KpiDto fromEntity(Kpi kpi) {
        if (kpi == null) {
            return null;
        }

        return KpiDto.builder()
                .id(kpi.getId())
                .nameKpi(kpi.getNameKpi())
                .label(kpi.getLabel())
                .description(kpi.getDescription())
                .type(kpi.getType())
                .activiteId(kpi.getActivites() != null ? kpi.getActivites().getId() : null) // Set activiteId if activite is not null
                .build();
    }

    public static Kpi toEntity(KpiDto dto) {
        if (dto == null) {
            return null;
        }

        Kpi kpi = new Kpi();
        kpi.setId(dto.getId());
        kpi.setNameKpi(dto.getNameKpi());
        kpi.setLabel(dto.getLabel());
        kpi.setDescription(dto.getDescription());
        kpi.setType(dto.getType());
        return kpi;
    }
}
