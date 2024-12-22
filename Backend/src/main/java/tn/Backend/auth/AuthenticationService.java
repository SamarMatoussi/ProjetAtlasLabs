package tn.Backend.auth;

import java.io.IOException;

import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;
import tn.Backend.config.JwtService;
import tn.Backend.entites.*;
import tn.Backend.repository.UserRepository;
import tn.Backend.services.EmailService;
import tn.Backend.services.ForgotPasswordService;
import tn.Backend.token.Token;
import tn.Backend.token.TokenRepository;
import tn.Backend.token.TokenType;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final ApplicationEventPublisher publisher;
  private final EmailService emailService;
  private final ForgotPasswordService forgotPasswordService;


  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    var user = repository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

    // Vérifiez si le compte est verrouillé
    if (user.getIsAccountLocked()) {
      // Envoi d'un e-mail de notification de verrouillage de compte
      emailService.sendAccountLockedEmail(user.getEmail());
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Votre compte est verrouillé en raison de trop nombreuses tentatives de connexion.");
    }

    try {
      authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      request.getEmail(),
                      request.getPassword()
              )
      );

      // Réinitialisez les tentatives de connexion après une authentification réussie
      user.setLoginAttempts(0);
      repository.save(user);

      // Générez les tokens
      var jwtToken = jwtService.generateToken(user);
      var refreshToken = jwtService.generateRefreshToken(user);
      revokeAllUserTokens(user);
      saveUserToken(user, jwtToken);

      return AuthenticationResponse.builder()
              .accessToken(jwtToken)
              .refreshToken(refreshToken)
              .role(user.getRole().name())
              .build();
    } catch (Exception ex) {
      // Incrémentez le compteur de tentatives
      int attempts = user.getLoginAttempts() + 1;
      user.setLoginAttempts(attempts);

      // Verrouillez le compte après 3 tentatives échouées
      if (attempts >= 3) {
        user.setIsAccountLocked(true);
        repository.save(user);

        // Envoi d'un e-mail de notification de verrouillage de compte
        emailService.sendAccountLockedEmail(user.getEmail());
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Votre compte est verrouillé en raison de trop nombreuses tentatives de connexion.");
      }

      repository.save(user);
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Échec de l'authentification : mauvais email ou mot de passe.");
    }
  }


  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
            .user(user)
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }
@PostConstruct
  public void createdefeultadm() {
    Administrateur user =new Administrateur();
    User savedUser = null;
    String email = "responsablesysteme@gmail.com";
    if (!repository.existsByEmail(email)) {
      user.setEmail("responsablesysteme@gmail.com");
      user.setPassword(new BCryptPasswordEncoder().encode("Administrateur123456*"));
      user.setFirstname("Admin");
      user.setLastname("Admin");
      user.setCin(14277034L);
      user.setPhone("98745639");
      user.setIsEnabled(true);
      user.setRole(Role.ADMIN);
      savedUser = repository.save((Administrateur) user);
    }

  }

  public void unlockAccount(String email) {
    var user = repository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

    user.setLoginAttempts(0);
    user.setIsAccountLocked(false);
    repository.save(user);
  }
  public Integer sendForgotPasswordToken(String email) {
    User user = repository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable avec cet email"));

    ForgotPasswordToken token = forgotPasswordService.createForgotPasswordToken(user);

    // Envoyer le code OTP par email
    emailService.sendForgotPasswordEmail(token);

    return token.getToken();
  }

  public void resetPassword(Integer token, String newPassword) {
    ForgotPasswordToken forgotPasswordToken = forgotPasswordService.validateForgotPasswordToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Token invalide ou expiré"));

    User user = forgotPasswordToken.getUser();
    user.setPassword(passwordEncoder.encode(newPassword));
    repository.save(user);

    // Supprimer le token utilisé
    forgotPasswordService.deleteToken(token);
  }



}
