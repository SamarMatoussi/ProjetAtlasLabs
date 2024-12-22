package tn.Backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.Backend.entites.Employe;
import tn.Backend.entites.Note;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class NoteDto {

    private Long id;
    private String appreciation;
    private Integer note;
    private LocalDate dateDeNotation;
    private Long employeId;
    private Long employeCin;
    private String employeEmail;
    private Long kpiId;
    private String nameKpi; // Nouveau champ ajouté pour le nom du KPI

    // Mapping d'une entité Note à un DTO
    public static NoteDto fromEntity(Note note) {
        if (note == null) {
            return null;
        }

        NoteDto dto = new NoteDto();
        dto.setId(note.getId());
        dto.setAppreciation(note.getAppreciation()); // Ajout de l'appréciation
        dto.setNote(note.getNote());
        dto.setDateDeNotation(note.getDateDeNotation());

        if (note.getEmploye() != null) {
            dto.setEmployeId(note.getEmploye().getId());
            dto.setEmployeCin(note.getEmploye().getCin());
            dto.setEmployeEmail(note.getEmploye().getEmail());
        }

        dto.setKpiId(note.getKpiId());
        dto.setNameKpi(note.getNameKpi()); // Mapping du champ nameKpi

        return dto;
    }

    // Mapping d'un DTO à une entité Note
    public static Note toEntity(NoteDto dto, Employe employe) {
        if (dto == null || employe == null) {
            return null;
        }

        Note note = Note.builder()
                .id(dto.getId())
                .appreciation(dto.getAppreciation()) // Ajout de l'appréciation
                .note(dto.getNote())
                .kpiId(dto.getKpiId())
                .nameKpi(dto.getNameKpi()) // Mapping du champ nameKpi
                .dateDeNotation(dto.getDateDeNotation() != null ? dto.getDateDeNotation() : LocalDate.now())
                .employe(employe)
                .build();

        return note;
    }

}
