package tn.Backend.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@Entity
@Table
@SuperBuilder
@DiscriminatorValue("Agent")
@NoArgsConstructor
public class Agent extends User {

    @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employe> employes;
}
