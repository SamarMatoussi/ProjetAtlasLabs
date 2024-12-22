package tn.Backend.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.Backend.dto.UserDto;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;


  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
          @RequestBody AuthenticationRequest request
  ) {
    return ResponseEntity.ok(service.authenticate(request));
  }
  @PatchMapping("/unlock")
  @PreAuthorize("hasAnyAuthority('AGENT', 'ADMIN','EMPLOYE')")
  public ResponseEntity<String> unlockAccount(@RequestParam String email) {
    service.unlockAccount(email);
    return ResponseEntity.ok("Compte déverrouillé avec succès");
  }
  @PostMapping("/refresh-token")
  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<String> forgotPassword(@RequestParam String email) {
    Integer token = service.sendForgotPasswordToken(email);
    return ResponseEntity.ok("Code de réinitialisation envoyé à votre adresse e-mail.");
  }


  @PostMapping("/reset-password")
  public ResponseEntity<String> resetPassword(@RequestParam Integer token, @RequestParam String newPassword) {
    service.resetPassword(token, newPassword);
    return ResponseEntity.ok("Mot de passe réinitialisé avec succès !");
  }

}
