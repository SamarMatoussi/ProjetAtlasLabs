package tn.Backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.Backend.entites.ForgotPasswordToken;
import tn.Backend.entites.User;
import tn.Backend.repository.ForgotPasswordTokenRepository;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final ForgotPasswordTokenRepository forgotPasswordTokenRepository;

    /**
     * Génère un OTP aléatoire à 6 chiffres.
     */
    public Integer generateToken() {
        return new Random().nextInt(900000) + 100000; // Génère un nombre entre 100000 et 999999
    }

    /**
     * Crée un token pour un utilisateur et remplace les anciens.
     */
    public ForgotPasswordToken createForgotPasswordToken(User user) {
        // Supprime les anciens tokens de l'utilisateur
        forgotPasswordTokenRepository.deleteByUser(user);

        // Crée un nouveau token avec une expiration dans 1 heure
        ForgotPasswordToken token = ForgotPasswordToken.builder()
                .token(generateToken())
                .user(user)
                .expirationTime(new Date(System.currentTimeMillis() + (60 * 60 * 1000))) // 1 heure
                .build();

        // Sauvegarde le token
        return forgotPasswordTokenRepository.save(token);
    }

    /**
     * Valide un token s'il existe et n'est pas expiré.
     */
    public Optional<ForgotPasswordToken> validateForgotPasswordToken(Integer token) {
        Optional<ForgotPasswordToken> forgotPasswordToken = forgotPasswordTokenRepository.findByToken(token);

        // Vérifie l'existence et l'expiration du token
        if (forgotPasswordToken.isPresent() &&
                forgotPasswordToken.get().getExpirationTime().after(new Date())) {
            return forgotPasswordToken;
        }
        return Optional.empty();
    }

    /**
     * Supprime un token après utilisation.
     */
    public void deleteToken(Integer token) {
        forgotPasswordTokenRepository.findByToken(token)
                .ifPresent(forgotPasswordTokenRepository::delete);
    }
}
