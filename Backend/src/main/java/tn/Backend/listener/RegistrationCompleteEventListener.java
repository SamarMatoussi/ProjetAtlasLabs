package tn.Backend.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import tn.Backend.dto.EmailDetails;
import tn.Backend.entites.Employe;
import tn.Backend.services.EmailService;
import tn.Backend.services.VerificationTokenService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    private final VerificationTokenService tokenService;
    private final EmailService emailService;
    private Employe user;
    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {

        // 1. Get the newly registered user
        user = event.getUser();

        // 2. Create a verification token for the user
        String verificationToken = UUID.randomUUID().toString();

        // 3. Save the verification token for the user
        tokenService.saveUserVerificationToken(user, verificationToken);

        // 4. Build the verification url to be sent to the user
        String url = event.getApplicationUrl() + "/api/v1/verify/email?token=" + verificationToken;

        // 5. Send the email
        sendVerificationEmail(url);

    }

    public void sendVerificationEmail(String url) {
        String subject = "Email Verification";
        String mailContent = "<p> Hi, " + user.getFirstname()  + ", </p>" +
                "<p>Thank you for registering with us, " +
                "Please, follow the link below to complete your registration.</p>" +
                "<a href=\"" + url + "\">Verify your email to activate your account</a>" +
                "<p> Thank you <br> Users Registration Portal Service";

        EmailDetails emailDetails = getEmailDetails(subject, mailContent);
        emailService.sendMail(emailDetails);
    }

    public void sendPasswordResetVerificationEmail(String url) {
        String subject = "Demande de réinitialisation de mot de passe";
        String content = "<html>"
                + "<head>"
                + "<style>"
                + "    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }"
                + "    .container { max-width: 600px; margin: auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); }"
                + "    .header { background-color: #007bff; color: white; padding: 10px 20px; border-radius: 8px 8px 0 0; text-align: center; }"
                + "    .footer { margin-top: 20px; font-size: 12px; color: #888; text-align: center; }"
                + "    h2 { margin: 0; font-size: 24px; }"
                + "    p { font-size: 14px; line-height: 1.6; }"
                + "    .button { display: inline-block; padding: 10px 20px; font-size: 16px; color: white; background-color: #007bff; text-decoration: none; border-radius: 5px; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<div class='header'>"
                + "<h2>Réinitialisation de votre mot de passe</h2>"
                + "</div>"
                + "<p>Bonjour " + user.getFirstname() + ",</p>"
                + "<p><b>Vous avez récemment demandé à réinitialiser votre mot de passe.</b></p>"
                + "<p>Pour procéder, veuillez suivre le lien ci-dessous :</p>"
                + "<a class='button' href=\"" + url + "\">Réinitialiser le mot de passe</a>"
                + "<p>Veuillez garder cet e-mail pour vos archives.</p>"
                + "<div class='footer'>"
                + "<p>Cordialement,<br>L'équipe de la plateforme</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        // Envoyer l'email à l'utilisateur
        EmailDetails emailDetails = EmailDetails.builder()
                .to(user.getEmail())
                .subject(subject)
                .messageBody(content)
                .build();

        emailService.sendMail(emailDetails);
    }


    private EmailDetails getEmailDetails(String subject, String mailContent) {
        return EmailDetails.builder()
                .subject(subject)
                .to(user.getEmail())
                .messageBody(mailContent)
                .build();
    }


}
