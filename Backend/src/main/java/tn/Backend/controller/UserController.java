package tn.Backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.Backend.dto.ChangePasswordResetRequest;
import tn.Backend.dto.UserDto;
import tn.Backend.entites.User;
import tn.Backend.services.PasswordResetTokenService;
import tn.Backend.services.UserService;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final PasswordResetTokenService passwordResetTokenService;

    @PreAuthorize("hasAnyAuthority('AGENT', 'ADMIN','EMPLOYE')")
    @GetMapping("/me")
    public ResponseEntity<UserDto> getProfile() {
        UserDto userDto = service.getProfile();
        return ResponseEntity.ok(userDto);
    }

    @PreAuthorize("hasAnyAuthority('AGENT', 'ADMIN', 'EMPLOYE')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateProfile(@PathVariable Long id, @RequestBody UserDto userDto) {
        UserDto updatedUser = service.updateProfile(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }
    @PreAuthorize("hasAnyAuthority('AGENT', 'ADMIN','EMPLOYE')")
    @GetMapping("/{email}")
    public User fetchUser(@PathVariable String email) {
        return service.fetchUser(email);
    }


    @PreAuthorize("hasAnyAuthority('AGENT', 'ADMIN','EMPLOYE')")
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email){

        return passwordResetTokenService.verifyEmail(email);

    }

    @PreAuthorize("hasAnyAuthority('AGENT', 'ADMIN','EMPLOYE')")
    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
        boolean isValid = passwordResetTokenService.verifyOtp(otp, email).hasBody();

        if (isValid) {
            return ResponseEntity.ok("OTP vérifié avec succès.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OTP incorrect ou expiré.");
        }
    }

    @PreAuthorize("hasAnyAuthority('AGENT', 'ADMIN','EMPLOYE')")
    @PostMapping("/changePassword/{email}")
    public ResponseEntity<Map<String, String>> changePasswordHandler(
            @RequestBody ChangePasswordResetRequest changePassword,
            @PathVariable String email
    ) {
        Map<String, String> response = new HashMap<>();

        if (changePassword.getNewPassword().length() < 8) {
            response.put("message", "Password must be at least 8 characters long.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        boolean isSuccess = passwordResetTokenService.changePasswordHandler(changePassword, email).hasBody();

        if (isSuccess) {
            response.put("message", "Password has been successfully changed!");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Failed to change the password. Please check your input and try again.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


}
