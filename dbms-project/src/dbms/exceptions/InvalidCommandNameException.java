package dbms.exceptions;

public class InvalidCommandNameException extends Exception {
    public InvalidCommandNameException(Exception e) {
        super(e);
    }

    public InvalidCommandNameException(String message, Exception e) {
        super(message, e);
    }

    public InvalidCommandNameException(String message) {
        super(message);
    }
}
