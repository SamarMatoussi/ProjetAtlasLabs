package tn.Backend.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import tn.Backend.dto.EmailDetails;
import tn.Backend.dto.EmployeDto;
import tn.Backend.dto.Response;
import tn.Backend.dto.UserDto;
import tn.Backend.entites.*;
import tn.Backend.repository.PosteRepository;
import tn.Backend.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService{
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PosteRepository posteRepository;

    @Override
    public Response addEmployeByAuthenticatedAgent(EmployeDto employeDto) {
        // Récupérer l'agent connecté
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User agent = repository.findByEmail(currentUsername)
                .filter(user -> user.getRole() == Role.AGENT)
                .orElseThrow(() -> new RuntimeException("Agent non trouvé ou non autorisé"));

        // Vérifications sur le CIN et l'email
        if (repository.existsByCin(employeDto.getCin())) {
            return Response.builder().responseMessage("Le numéro CIN est déjà utilisé!").build();
        }
        if (repository.existsByEmail(employeDto.getEmail())) {
            return Response.builder().responseMessage("L'email fourni est déjà utilisé!").build();
        }

        // Récupérer le poste depuis le posteId
        Poste poste = null;
        if (employeDto.getPosteId() != null) {
            poste = posteRepository.findById(employeDto.getPosteId())
                    .orElseThrow(() -> new RuntimeException("Poste introuvable"));
        }

        // Création de l'employé
        Employe employe = Employe.builder()
                .firstname(employeDto.getFirstname())
                .lastname(employeDto.getLastname())
                .cin(employeDto.getCin())
                .email(employeDto.getEmail())
                .phone(employeDto.getPhone())
                .password(passwordEncoder.encode(employeDto.getPassword()))
                .role(Role.EMPLOYE)
                .agent((Agent) agent)
                .poste(poste)
                .isEnabled(true)
                .build();

        repository.save(employe);
        sendAgentRegistrationEmail(employeDto, null);

        return Response.builder()
                .responseMessage("Employé ajouté avec succès par l'agent connecté")
                .email(employe.getEmail())
                .build();
    }


    @Override
    public List<EmployeDto> getEmployesByAuthenticatedAgent() {
        // Récupérer l'agent connecté
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User agent = repository.findByEmail(currentUsername)
                .filter(user -> user.getRole() == Role.AGENT)
                .orElseThrow(() -> new RuntimeException("Agent non trouvé ou non autorisé"));

        // Récupérer les employés associés à cet agent
        List<Employe> employes = repository.findAllByAgent_Id(agent.getId());

        if (employes.isEmpty()) {
            throw new RuntimeException("Aucun employé trouvé pour cet agent.");
        }

        // Mapper les employés en DTOs avec les détails du posteId
        return employes.stream()
                .map(EmployeDto::fromEntity)
                .collect(Collectors.toList());
    }



    @Override
    public Response updateEmployeByAuthenticatedAgent(Long cin, EmployeDto employeDto) {
        // Récupérer l'agent connecté
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User agent = repository.findByEmail(currentUsername)
                .filter(user -> user.getRole() == Role.AGENT)
                .orElseThrow(() -> new RuntimeException("Agent non trouvé ou non autorisé"));

        // Rechercher l'employé par CIN et vérifier qu'il appartient à l'agent
        Employe employe = (Employe) repository.findEmployeByCinAndAgent_Id(cin, agent.getId())
                .orElseThrow(() -> new RuntimeException("Employé non trouvé ou non autorisé pour modification."));

        // Mettre à jour les informations de l'employé
        employe.setFirstname(employeDto.getFirstname());
        employe.setLastname(employeDto.getLastname());
        employe.setEmail(employeDto.getEmail());
        employe.setPhone(employeDto.getPhone());


        // Enregistrer les modifications
        repository.save(employe);

        return Response.builder()
                .responseMessage("Les informations de l'employé avec le CIN " + cin + " ont été mises à jour avec succès.")
                .build();
    }


    @Override
    public Response updateEmploye(Long cin, EmployeDto employeDto) {
        // Rechercher l'employé par CIN
        Optional<User> userOptional = repository.findUserByCin(cin);

        if (userOptional.isEmpty()) {
            return Response.builder()
                    .responseMessage("Employé non trouvé pour le CIN spécifié")
                    .build();
        }

        // Extraire l'employé de l'Optional
        User employe = userOptional.get();

        // Vérifier si l'utilisateur a le rôle d'employé
        if (employe.getRole() != Role.EMPLOYE) {
            return Response.builder()
                    .responseMessage("Impossible de mettre à jour les informations. L'utilisateur n'est pas un employé.")
                    .build();
        }

        // Vérifier si un agent ID est fourni dans EmployeDto
        if (employeDto.getAgentId() != null) {
            Optional<User> agentOptional = repository.findById(employeDto.getAgentId());

            // Vérifier si l'agent existe et a le rôle AGENT
            if (agentOptional.isEmpty() || agentOptional.get().getRole() != Role.AGENT) {
                return Response.builder()
                        .responseMessage("L'agent spécifié est invalide ou n'a pas le rôle d'agent.")
                        .build();
            }

            // Associer l'agent valide à l'employé
            employe.setAgent(agentOptional.get());
        }

        // Mettre à jour les informations de l'employé
        employe.setFirstname(employeDto.getFirstname());
        employe.setLastname(employeDto.getLastname());
        employe.setEmail(employeDto.getEmail());
        employe.setPhone(employeDto.getPhone());

        // Enregistrer les changements
        repository.save(employe);

        return Response.builder()
                .responseMessage("Les informations de l'employé avec le CIN " + cin + " ont été mises à jour avec succès.")
                .build();
    }

    @Override
    public List<UserDto> getEmployesByAgent(Long agentId) {
        List<User> employes = repository.findAllByRoleAndAgent_Id(Role.EMPLOYE, agentId);

        if (employes.isEmpty()) {
            throw new RuntimeException("Aucun employé trouvé pour l'agent ID : " + agentId);
        }

        return employes.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }


    @Override
    public Optional<User> findUserByIdAndRole(Long id, Role role) {
        return repository.findByIdAndRole(id, role);
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

        // Vérifier si l'utilisateur a le rôle d'employé
        if (user.getRole() != Role.EMPLOYE) {
            return Response.builder()
                    .responseMessage("Impossible de révoquer ou d'activer le compte. L'utilisateur n'est pas un employé.")
                    .build();
        }

        // Révoquez ou activez le compte de l'utilisateur en fonction du boolean "activate"
        user.setIsEnabled(activate);
        repository.save(user);

        // Envoyer un email pour informer l'utilisateur du changement de statut
        sendAccountStatusEmail(user, activate);

        if (activate) {
            return Response.builder()
                    .responseMessage("Le compte de l'employé avec le CIN " + cin + " a été activé avec succès")
                    .build();
        } else {
            return Response.builder()
                    .responseMessage("Le compte de l'employé avec le CIN " + cin + " a été révoqué avec succès")
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
                + "<p>Bonjour <strong>" + HtmlUtils.htmlEscape(user.getFirstname()) + "</strong>,</p>"
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


    private void sendAgentRegistrationEmail(EmployeDto employeDto, HttpServletRequest request) {
        // Sujet de l'email
        String subject = "Bienvenue sur notre plateforme";

        // Contenu de l'email en HTML
        StringBuilder content = new StringBuilder();
        content.append("<html>")
                .append("<head>")
                .append("<style>")
                .append("    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; }")
                .append("    .container { max-width: 600px; margin: auto; padding: 20px; }")
                .append("    .footer { margin-top: 20px; font-size: 12px; color: #888; text-align: center; }")
                .append("    h2 { font-size: 24px; }")
                .append("    p { font-size: 14px; line-height: 1.6; }")
                .append("    .bold { font-weight: bold; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<div class='container'>")
                .append("<h2>Bienvenue sur notre plateforme</h2>")
                .append("<p>Chère " + HtmlUtils.htmlEscape(employeDto.getFirstname()) + ",</p>")
                .append("<p>Nous sommes ravis de vous accueillir parmi nous !</p>")
                .append("<p>Un agent a créé votre compte avec succès. Voici vos informations de connexion :</p>")
                .append("<p>Email : <span class='bold'>" + HtmlUtils.htmlEscape(employeDto.getEmail()) + "</span></p>")
                .append("<p>Mot de passe temporaire : <span class='bold'>" + HtmlUtils.htmlEscape(employeDto.getPassword()) + "</span></p>")
                .append("<p>Poste affecté : <span class='bold'>" + (employeDto.getPosteId() != null ? HtmlUtils.htmlEscape(posteRepository.findById(employeDto.getPosteId()).get().getName()) : "Aucun poste affecté") + "</span></p>")
                .append("<p>Veuillez noter qu'il s'agit de votre mot de passe temporaire. ")
                .append("Nous vous recommandons fortement de le modifier immédiatement après votre première connexion.</p>")
                .append("<p>N'oubliez pas de conserver cet e-mail pour vos archives.</p>")
                .append("<div class='footer'>")
                .append("<p>Cordialement,<br>L'équipe de la plateforme</p>")
                .append("</div>")
                .append("</div>")
                .append("</body>")
                .append("</html>");

        // Envoyer l'email à l'employé
        EmailDetails emailDetails = EmailDetails.builder()
                .to(employeDto.getEmail())
                .subject(subject)
                .messageBody(content.toString())
                .build();

        emailService.sendMail(emailDetails);
    }

    @Override
    public List<UserDto> getAllEmployes() {
        List<User> allEmployes = repository.findAllByRole(Role.EMPLOYE);
        return allEmployes.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }
    @Override
    public Response deleteEmploye(Long cin) {
        Optional<User> userOptional = repository.findUserByCin(cin);

        if (userOptional.isEmpty()) {
            return Response.builder()
                    .responseMessage("Employé non trouvé pour le CIN spécifié")
                    .build();
        }

        // Supprimer l'employé de la base de données
        repository.delete(userOptional.get());

        return Response.builder()
                .responseMessage("L'employé avec le CIN " + cin + " a été supprimé avec succès")
                .build();
    }
    @Override
    public Response deleteUser(Long id) {
        // Recherchez l'utilisateur par ID dans votre système
        Optional<User> userOptional = repository.findById(id);

        if (userOptional.isEmpty()) {
            return Response.builder()
                    .responseMessage("Employé non trouvé pour l'ID spécifié")
                    .build();
        }

        // Supprimez l'utilisateur
        repository.deleteById(id);

        return Response.builder()
                .responseMessage("L'employé avec l'ID " + id + " a été supprimé avec succès")
                .build();
    }
}
