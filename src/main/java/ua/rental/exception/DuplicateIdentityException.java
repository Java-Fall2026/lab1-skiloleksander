package ua.rental.exception;

public class DuplicateIdentityException extends IllegalStateException {
    public DuplicateIdentityException(String message) {
        super(message);
    }

    public DuplicateIdentityException(String message, Throwable cause) {
        super(message, cause);
    }
}
