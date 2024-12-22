package tn.Backend.entites;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tn.Backend.token.Token;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "USER_TYPE", discriminatorType = DiscriminatorType.STRING)
public class User extends AbstractEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private Long cin;
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private String phone;

    @OneToOne
    @JoinColumn(name = "agence_id")
    private Agence agence;

    @ManyToOne // Relation ManyToOne, un agent peut être responsable de plusieurs employés
    @JoinColumn(name = "agent_id") // Nom de la colonne qui va référencer l'agent
    private User agent;

    @Builder.Default
    private Boolean isEnabled = false;

    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @Override
   public Collection<? extends GrantedAuthority> getAuthorities() {
       return List.of(new SimpleGrantedAuthority(role.name()));
   }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Builder.Default
    private Integer loginAttempts = 0;

    @Builder.Default
    private Boolean isAccountLocked = false;

    @Override
    public boolean isAccountNonLocked() {
        return !isAccountLocked;
    }
}
