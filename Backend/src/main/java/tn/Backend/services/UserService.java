package tn.Backend.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.Backend.dto.UserDto;
import tn.Backend.entites.ForgotPasswordToken;
import tn.Backend.entites.User;
import tn.Backend.repository.UserRepository;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;


    public UserDto getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Récupère l'email de l'utilisateur connecté
        User user = repository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        return UserDto.fromEntity(user);
    }

    public UserDto updateProfile(Long id, UserDto userDto) {
        User existingUser = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Mettre à jour les informations
        existingUser.setFirstname(userDto.getFirstname());
        existingUser.setLastname(userDto.getLastname());
        existingUser.setPhone(userDto.getPhone());
        existingUser.setEmail(userDto.getEmail());

        repository.save(existingUser); // Sauvegarder les modifications

        return UserDto.fromEntity(existingUser);  // Retourner l'utilisateur mis à jour
    }
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);
    }
    public User fetchUser(String email) {
        return repository.existsByEmail(email)
                ? repository.findByEmail(email).get()
                : null;
    }






    public static String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }


}
