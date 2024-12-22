package tn.Backend.services;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import tn.Backend.dto.EmailDetails;
import tn.Backend.entites.ForgotPasswordToken;

@Service
@RequiredArgsConstructor
public class EmailService {


    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;


    public String sendSimpleMail(EmailDetails emailDetails) {

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            //Java mail sender takes an argument of type SimpleMailMessage only

            mailMessage.setFrom(sender);
            mailMessage.setTo(emailDetails.getTo());
            mailMessage.setSubject(emailDetails.getSubject());
            mailMessage.setText(emailDetails.getMessageBody());

            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully...";
        } catch (MailException e) {
            throw new MailException("erreur") {
            };
        }


    }


    public void sendMail(EmailDetails emailDetails) {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(emailDetails.getTo());
            mimeMessageHelper.setText(emailDetails.getMessageBody(), true);
            mimeMessageHelper.setSubject(emailDetails.getSubject());

            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new tn.Backend.exception.MailException(e.getMessage());
        }

    }

    public void sendAccountLockedEmail(String email) {
        // Création du message MIME
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            // Définition de l'expéditeur et du destinataire
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(email);

            // Sujet de l'email
            mimeMessageHelper.setSubject("Alerte de sécurité : Compte verrouillé");

            // Corps du message en HTML
            String messageBody = "<html><body>" +
                    "<h2>Votre compte a été verrouillé</h2>" +
                    "<p>Bonjour,</p>" +
                    "<p>Nous avons détecté plusieurs tentatives de connexion échouées à votre compte, et en raison de cela, votre compte a été temporairement verrouillé pour des raisons de sécurité.</p>" +
                    "<p>Pour rétablir l'accès à votre compte, veuillez contacter l'administrateur ou réinitialiser votre mot de passe en suivant les instructions de récupération de mot de passe sur notre site.</p>" +
                    "<p>Si vous avez des questions, n'hésitez pas à contacter notre support.</p>" +
                    "<p>Cordialement,</p>" +
                    "<p>L'équipe de sécurité de notre application.</p>" +
                    "</body></html>";

            // Définir le corps du message HTML
            mimeMessageHelper.setText(messageBody, true);

            // Envoi de l'email
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new tn.Backend.exception.MailException("Erreur lors de l'envoi de l'e-mail de verrouillage du compte : " + e.getMessage());
        }
    }

    public void sendForgotPasswordEmail(ForgotPasswordToken forgotPasswordToken) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            // Définir l'expéditeur et le destinataire
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(forgotPasswordToken.getUser().getEmail());
            mimeMessageHelper.setSubject("Code de réinitialisation de votre mot de passe");

            // Code OTP
            int otpCode = forgotPasswordToken.getToken();

            // Corps du message
            String messageBody = String.format("""
                    <html>
                    <body>
                        <h2>Code de réinitialisation de votre mot de passe</h2>
                        <p>Bonjour %s,</p>
                        <p>Nous avons reçu une demande de réinitialisation de mot de passe pour votre compte.</p>
                        <p>Voici votre code de réinitialisation : <strong>%d</strong></p>
                        <p>Ce code est valide pour une durée limitée.</p>
                        <p>Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.</p>
                        <p>Cordialement,</p>
                        <p>L'équipe de sécurité de notre application.</p>
                    </body>
                    </html>
                    """, forgotPasswordToken.getUser().getFirstname(), otpCode);

            mimeMessageHelper.setText(messageBody, true); // true pour HTML

            // Envoyer l'email
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new tn.Backend.exception.MailException("Erreur lors de l'envoi de l'email de réinitialisation : " + e.getMessage());
        }
    }
}