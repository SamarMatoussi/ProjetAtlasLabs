package tn.Backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.Backend.dto.EmployeDto;

import tn.Backend.dto.Response;
import tn.Backend.dto.UserDto;
import tn.Backend.entites.Employe;
import tn.Backend.entites.Role;
import tn.Backend.entites.User;
import tn.Backend.services.AgentService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/agent")
@RequiredArgsConstructor
public class AgentController {
    private final AgentService agentService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT')")
    @PostMapping("/addEmployeByAgent")
    public ResponseEntity<?> addEmployeByAgent(@RequestBody @Valid EmployeDto employeDto) {
        try {
            Response response = agentService.addEmployeByAuthenticatedAgent(employeDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Erreur interne du serveur."));
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT')")
    @GetMapping("/myEmployes")
    public ResponseEntity<?> getEmployesByAuthenticatedAgent() {
        try {
            List<EmployeDto> employes = agentService.getEmployesByAuthenticatedAgent();
            return ResponseEntity.ok(employes);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Erreur interne du serveur."));
        }
    }
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT')")
    @PutMapping("/updateMyEmploye/{cin}")
    public ResponseEntity<?> updateEmployeByAuthenticatedAgent(@PathVariable Long cin, @RequestBody @Valid EmployeDto employeDto) {
        try {
            Response response = agentService.updateEmployeByAuthenticatedAgent(cin, employeDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Erreur interne du serveur."));
        }
    }
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT')")
    @PutMapping("/updateemploye/{cin}")
    public ResponseEntity<Response> updateEmploye(@PathVariable Long cin, @RequestBody @Valid EmployeDto employeDto) {
        Response response = agentService.updateEmploye(cin, employeDto);
        return ResponseEntity.ok(response);
    }





    @PreAuthorize("hasAnyAuthority('AGENT', 'ADMIN')")
    @GetMapping("/employes/{userId}")
    public ResponseEntity<UserDto> getEmployeeById(@PathVariable Long userId) {
        Optional<User> userOptional = agentService.findUserByIdAndRole(userId, Role.EMPLOYE);
        return userOptional.map(user -> ResponseEntity.ok(UserDto.fromEntity(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('AGENT', 'ADMIN')")
    @PutMapping("/revokeaccountemploye")
    public ResponseEntity<Response> revokeAccount(@RequestParam Long cin, @RequestParam boolean activate) {
        Response response = agentService.revokeAccount(cin, activate);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('AGENT', 'ADMIN')")
    @GetMapping("/allemployes")
    public ResponseEntity<List<UserDto>> getAllEmployes() {
        List<UserDto> employes = agentService.getAllEmployes();
        return ResponseEntity.ok(employes);
    }
    @PreAuthorize("hasAuthority('AGENT')")
    @DeleteMapping("/deleteemploye/{cin}")
    public ResponseEntity<Response> deleteEmploye(@PathVariable Long cin) {
        Response response = agentService.deleteEmploye(cin);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('AGENT')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteUser(@PathVariable Long id) {
        Response response = agentService.deleteUser(id);
        return ResponseEntity.ok(response);
    }

}
