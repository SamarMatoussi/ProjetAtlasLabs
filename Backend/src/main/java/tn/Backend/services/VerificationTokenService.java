package tn.Backend.services;

import org.springframework.http.ResponseEntity;
import tn.Backend.dto.Response;
import tn.Backend.entites.User;
import tn.Backend.entites.VerificationToken;

public interface VerificationTokenService {
    void saveUserVerificationToken(User user, String token);
    String validateToken(String token);
    ResponseEntity<Response> verifyEmail(String token);
    VerificationToken generateNewVerificationToken(String oldToken);
}