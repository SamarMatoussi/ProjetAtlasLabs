package tn.Backend.dto;

import lombok.Builder;
import lombok.Data;
import tn.Backend.dto.AgenceDto;
import tn.Backend.entites.Agence;
import tn.Backend.entites.Role;
import tn.Backend.entites.User;

@Data
@Builder
public class UserDto {

    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private Long cin;
    private String password;
    private Role role;
    private boolean isEnabled;
    private Long agenceId;

    public static UserDto fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return UserDto.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .cin(user.getCin())
                .role(user.getRole())
                .isEnabled(user.isEnabled())
                .agenceId(user.getAgence() != null ? user.getAgence().getId() : null) // Extraire seulement l'ID
                .build();
    }

    public static User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.getId());
        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setCin(dto.getCin());
        user.setRole(dto.getRole());
        user.setIsEnabled(dto.isEnabled());

        if (dto.getAgenceId() != null) {
            Agence agence = new Agence();
            agence.setId(dto.getAgenceId()); // Assigner seulement l'ID
            user.setAgence(agence);
        }

        return user;
    }
}
