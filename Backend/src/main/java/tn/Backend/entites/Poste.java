package tn.Backend.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Poste {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;
        private String description;

      /*  @ManyToMany
        @JoinTable(
                name = "poste_activite",
                joinColumns = @JoinColumn(name = "poste_id"),
                inverseJoinColumns = @JoinColumn(name = "activite_id"))
        private List<Activites> activites;*/

    @OneToMany(mappedBy = "poste", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employe> employes;

    // Getters and setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        /*public List<Activites> getActivites() {
            return activites;
        }

        public void setActivites(List<Activites> activites) {
            this.activites = activites;
        }*/

}