package tn.Backend.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table
@SuperBuilder
@NoArgsConstructor
public class Note extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String appreciation ;

    private Integer note;
    private String nameKpi; // Nouveau champ ajouté pour le nom du KPI

    private LocalDate dateDeNotation;

    private Long agentId ;

    private Long kpiId ;

    @ManyToOne
    @JoinColumn(name = "employe_id")
    private Employe employe;


}
