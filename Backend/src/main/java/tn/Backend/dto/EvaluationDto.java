package tn.Backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvaluationDto {

    private Long employeId;
    private Long kpiId;
    private Integer note;
    private String appreciation;
}
