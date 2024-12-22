package tn.Backend.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.Backend.dto.*;
import tn.Backend.entites.*;
import tn.Backend.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AdministrateurServiceImpl implements AdministrateurService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher publisher;
    private final EmailService emailService;

    @Override
    public Response addAgent(AgentDto agentDto) {
        // Vérification si l'utilisateur existe déjà avec cet email
        boolean userExists = repository.existsByEmail(agentDto.getEmail());
        if (userExists) {
            return Response.builder()
                    .responseMessage("User with provided email already exists!")
                    .build();
        }

        // Vérification si l'agence est déjà assignée à un autre utilisateur
        if (agentDto.getAgenceId() != null) {
            boolean agenceAssigned = repository.existsByAgenceId(agentDto.getAgenceId());
            if (agenceAssigned) {
                return Response.builder()
                        .responseMessage("The selected agency is already assigned to another user!")
                        .build();
            }
        }

        // Conversion du DTO en entité Agent
        Agent agent = AgentDto.toEntity(agentDto);
        agent.setPassword(passwordEncoder.encode(agent.getPassword()));
        agent.setRole(Role.AGENT);

        // Persistance de l'utilisateur
        User savedUser = repository.save(agent);

        // Envoi de l'email à l'agent
        sendAgentRegistrationEmail(agentDto, null);

        // Retour de la réponse avec l'ID de l'agence
        return Response.builder()
                .responseMessage("Agent registered successfully!")
                .email(savedUser.getEmail())
               // .agenceId(agentDto.getAgenceId()) // Inclure l'ID de l'agence dans la réponse
                .build();
    }



    private void sendAgentRegistrationEmail(AgentDto agentDto, HttpServletRequest request) {
        // Construire le contenu de l'email pour l'agent en HTML
        String subject = "Création de votre compte agent";
        String content = "<html>"
                + "<head>"
                + "<style>"
                + "    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }"
                + "    .container { max-width: 600px; margin: auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); }"
                + "    .header { background-color: #007bff; color: white; padding: 10px 20px; border-radius: 8px 8px 0 0; text-align: center; }"
                + "    .footer { margin-top: 20px; font-size: 12px; color: #888; text-align: center; }"
                + "    h2 { margin: 0; font-size: 24px; }"
                + "    p { font-size: 14px; line-height: 1.6; }"
                + "    .info { background-color: #e9ecef; padding: 10px; border-radius: 5px; margin: 20px 0; }"
                + "    .bold { font-weight: bold; }"
                + "    ul { padding-left: 20px; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<div class='header'>"
                + "<h2>Bienvenue sur notre plateforme</h2>"
                + "</div>"
                + "<p>Chère " + agentDto.getFirstname() + ",</p>"
                + "<p>Votre compte agent a été créé avec succès par l'administrateur.</p>"
                + "<p>Voici vos informations de connexion :</p>"
                + "<div class='info'>"
                + "<ul>"
                + "    <li>Email : <span class='bold'>" + agentDto.getEmail() + "</span></li>"
                + "    <li>Mot de passe temporaire : <span class='bold'>" + agentDto.getPassword() + "</span></li>"
                + "</ul>"
                + "</div>"
                + "<p>Veuillez noter qu'il s'agit de votre mot de passe temporaire. "
                + "Pour des raisons de sécurité, nous vous recommandons vivement de le modifier dès votre première connexion.</p>"
                + "<p>Veuillez conserver cet e-mail pour vos archives.</p>"
                + "<div class='footer'>"
                + "<p>Cordialement,<br>L'équipe de la plateforme</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        // Envoyer l'email à l'agent
        EmailDetails emailDetails = EmailDetails.builder()
                .to(agentDto.getEmail())
                .subject(subject)
                .messageBody(content)
                .build();

        emailService.sendMail(emailDetails);
    }


    @Override
    public Response revokeAccount(Long cin, boolean activate) {
        // Recherchez l'utilisateur par CIN dans votre système
        Optional<User> userOptional = repository.findUserByCin(cin);

        if (userOptional.isEmpty()) {
            return Response.builder()
                    .responseMessage("Utilisateur non trouvé pour le CIN spécifié")
                    .build();
        }

        // Extraire l'utilisateur de l'Optional
        User user = userOptional.get();

        // Révoquez ou activez le compte de l'utilisateur en fonction du boolean "activate"
        user.setIsEnabled(activate);
        repository.save(user);

        // Envoyer un email à l'agent pour l'informer du changement de statut
        sendAccountStatusEmail(user, activate);

        if (activate) {
            return Response.builder()
                    .responseMessage("Le compte de l'utilisateur avec le CIN " + cin + " a été activé avec succès")
                    .build();
        } else {
            return Response.builder()
                    .responseMessage("Le compte de l'utilisateur avec le CIN " + cin + " a été révoqué avec succès")
                    .build();
        }
    }

    private void sendAccountStatusEmail(User user, boolean activate) {
        String subject = activate ? "Activation de votre compte" : "Révocation de votre compte";
        String statusMessage = activate ? "activé" : "révoqué";

        // Contenu de l'email avec un style moderne
        String content = "<html>"
                + "<head>"
                + "<style>"
                + "    body { font-family: 'Arial', sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }"
                + "    .container { max-width: 600px; margin: auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); }"
                + "    .header { background-color: #007bff; color: white; padding: 15px; border-radius: 8px 8px 0 0; text-align: center; }"
                + "    .footer { margin-top: 20px; font-size: 12px; color: #888; text-align: center; }"
                + "    h2 { margin: 0; font-size: 20px; }"
                + "    p { font-size: 14px; line-height: 1.5; }"
                + "    .status { font-size: 16px; font-weight: bold; color: #007bff; }"
                + "    .note { margin: 20px 0; font-size: 12px; color: #555; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<div class='header'>"
                + "<h2>Notification de Compte</h2>"
                + "</div>"
                + "<p>Bonjour <strong>" + user.getFirstname() + "</strong>,</p>"
                + "<p>Nous souhaitons vous informer que votre compte a été <span class='status'>" + statusMessage + "</span> avec succès.</p>"
                + "<p>Si vous avez des questions ou des préoccupations, n'hésitez pas à nous contacter.</p>"
                + "<p class='note'>Merci de votre confiance.</p>"
                + "<div class='footer'>"
                + "<p>Cordialement,<br>L'équipe de la plateforme</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        // Préparer les détails de l'email
        EmailDetails emailDetails = EmailDetails.builder()
                .to(user.getEmail())
                .subject(subject)
                .messageBody(content)
                .build();

        // Envoyer l'email
        emailService.sendMail(emailDetails);
    }






    @Override
    public List<UserDto> getAllUsersExceptAdmin() {
        List<User> allUsers = repository.findAll();
        // Exclure les utilisateurs ayant le rôle ADMINISTRATEUR
        return allUsers.stream()
                .filter(user -> user.getRole() != Role.ADMIN)
                .filter(user -> user.getRole() != Role.EMPLOYE)
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Response updateUser(User user) {
        try {
            repository.save(user);
            return Response.builder()
                    .responseMessage("L'utilisateur a été mis à jour avec succès")
                    .build();
        } catch (Exception e) {
            return Response.builder()
                    .responseMessage("Une erreur s'est produite lors de la mise à jour de l'utilisateur")
                    .build();
        }
    }

    @Override
    public Response deleteUser(Long id) {
        // Recherchez l'utilisateur par ID dans votre système
        Optional<User> userOptional = repository.findById(id);

        if (userOptional.isEmpty()) {
            return Response.builder()
                    .responseMessage("Utilisateur non trouvé pour l'ID spécifié")
                    .build();
        }

        // Supprimez l'utilisateur
        repository.deleteById(id);

        return Response.builder()
                .responseMessage("L'utilisateur avec l'ID " + id + " a été supprimé avec succès")
                .build();
    }
    @Override
    public List<UserDto> getAllAdmin() {
        List<User> allUsers = repository.findAll();
        // Filter users with the role ADMIN
        List<UserDto> adminUsers = allUsers.stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
        return adminUsers;
    }

}