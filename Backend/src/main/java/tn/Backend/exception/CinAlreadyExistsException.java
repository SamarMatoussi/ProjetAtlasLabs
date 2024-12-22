package tn.Backend.exception;

public class CinAlreadyExistsException extends RuntimeException {
    public CinAlreadyExistsException(String message) {
        super(message);
    }
}
