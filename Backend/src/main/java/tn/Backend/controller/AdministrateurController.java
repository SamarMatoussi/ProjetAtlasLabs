package tn.Backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.Backend.dto.AgentDto;
import tn.Backend.dto.Response;
import tn.Backend.dto.UserDto;
import tn.Backend.entites.User;
import tn.Backend.services.AdministrateurService;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdministrateurController {
    private final AdministrateurService administrateurService;

    
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/addagent")
    public Response addAgent(
            @RequestBody @Valid AgentDto userRequest

    )  {
        return administrateurService.addAgent(userRequest);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/revokeaccount")
    public ResponseEntity<Response> revokeAccount(@RequestParam Long cin, @RequestParam boolean activate) {
        Response response = administrateurService.revokeAccount(cin, activate);
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/updateuser/{userId}")
    public ResponseEntity<Response> updateUser(@PathVariable Long userId, @RequestBody @Valid UserDto userDto) {
        Optional<User> userOptional = administrateurService.findUserById(userId);
        if (userOptional.isPresent()) {
            User userToUpdate = userOptional.get();
            // Mettre à jour les champs modifiables de l'utilisateur
            userToUpdate.setFirstname(userDto.getFirstname());
            userToUpdate.setLastname(userDto.getLastname());
            userToUpdate.setEmail(userDto.getEmail());
            userToUpdate.setPhone(userDto.getPhone());
            userToUpdate.setPassword(userDto.getPassword());

            // Appeler le service pour effectuer la mise à jour
            Response response = administrateurService.updateUser(userToUpdate);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteUser(@PathVariable Long id) {
        Response response = administrateurService.deleteUser(id);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT')")
    @GetMapping("/usersexceptadmin")
    public ResponseEntity<List<UserDto>> getAllUsersExceptAdmin() {
        List<UserDto> users = administrateurService.getAllUsersExceptAdmin();
        return ResponseEntity.ok(users);
    }



    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        Optional<User> userOptional = administrateurService.findUserById(userId);
        return userOptional.map(user ->ResponseEntity.ok(UserDto.fromEntity(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/allAdmins")
    public ResponseEntity<List<UserDto>> getAllAdmins() {
        List<UserDto> adminUsers = administrateurService.getAllAdmin();
        return new ResponseEntity<>(adminUsers, HttpStatus.OK);
    }
}

