package tn.Backend.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table
@SuperBuilder
@DiscriminatorValue("Administrateur")
@NoArgsConstructor
public class Administrateur extends User {

}


