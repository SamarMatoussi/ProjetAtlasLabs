package tn.Backend.entites;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.Backend.dto.KpiDto;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Kpi extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column( unique = true)
    private String nameKpi;
    @Column(unique = true)
    private String label;
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private KpiType type;

    //@Column
    //private String optionValue;

    @ManyToOne
    @JoinColumn(name = "activites_id" , referencedColumnName = "id")
    private Activites activites;

    @JsonIgnoreProperties(value = "kpi" )
    @OneToMany(mappedBy = "kpi", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParametrageKpi> parametrages;


/*
    @ManyToOne
    private Agence agence;*/
}
