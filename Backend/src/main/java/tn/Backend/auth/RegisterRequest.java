package tn.Backend.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.Backend.entites.Role;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  private String firstname;
  private String lastname;
  private Long cin;
  private String phone;
  private String email;
  private String password;
  private Role role;
  private Boolean isEnabled;

}
