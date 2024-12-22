package tn.Backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteGlobaleDto {
    private Long cin;
    private Double noteGlobale;
    private String appreciation;

    public NoteGlobaleDto(Long cin, Double noteGlobale) {
        this.cin = cin;
        this.noteGlobale = noteGlobale;
        this.appreciation = calculateAppreciation(noteGlobale);
    }

    private String calculateAppreciation(Double noteGlobale) {
        if (noteGlobale <= 1.50) {
            return "Mauvais";
        } else if (noteGlobale <= 2.50) {
            return "Passable";
        } else if (noteGlobale <= 3.50) {
            return "Bien";
        } else {
            return "Excellent";
        }
    }
}
