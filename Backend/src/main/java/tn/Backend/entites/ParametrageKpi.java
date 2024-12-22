package tn.Backend.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "parametrage_kpi")
public class ParametrageKpi {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "appréciation")
    private Appreciation appreciation;

    // Champs pour le type NUMERIQUE
    private Long max;
    private Long min;

    // Champs pour le type STRING
    private String utilite;

    @ManyToOne
    @JoinColumn(name = "kpi_id")
    private Kpi kpi;

    public boolean isNumeric() {
        return kpi != null && KpiType.NUMERIQUE.equals(kpi.getType());
    }

    public boolean isString() {
        return kpi != null && KpiType.STRING.equals(kpi.getType());
    }
}
