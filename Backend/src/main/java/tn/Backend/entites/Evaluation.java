package tn.Backend.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User employe;

    @ManyToOne
    private Kpi kpi;

    private Integer note;

    @Column(length = 500)
    private String appreciation;
}
