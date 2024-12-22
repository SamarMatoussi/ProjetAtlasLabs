package tn.Backend.dto;


import lombok.Getter;
import lombok.Setter;
import tn.Backend.auth.RegisterRequest;
import tn.Backend.entites.Administrateur;



@Getter
@Setter
public class AdministrateurDto extends RegisterRequest {

    public static Administrateur toEntity(AdministrateurDto request) {
        return Administrateur.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .cin(request.getCin())
                .password(request.getPassword())
                .email(request.getEmail())
                .role(request.getRole())
                .phone(request.getPhone())
                .build();
    }


}
