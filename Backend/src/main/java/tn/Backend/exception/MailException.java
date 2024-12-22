package tn.Backend.exception;

public class MailException extends org.springframework.mail.MailException {
    public MailException(String message) {
        super(message);
    }
}