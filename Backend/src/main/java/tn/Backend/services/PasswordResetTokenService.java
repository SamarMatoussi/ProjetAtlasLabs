package tn.Backend.services;

import org.springframework.http.ResponseEntity;
import tn.Backend.dto.ChangePasswordResetRequest;

public interface PasswordResetTokenService {
    ResponseEntity<String> verifyEmail(String email);

    ResponseEntity<String> verifyOtp(Integer otp, String email);

    ResponseEntity<String> changePasswordHandler(
            ChangePasswordResetRequest changePasswordResetRequest,
            String email
    );}