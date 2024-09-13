package dbms.exceptions;

public class InvalidArgsException extends Exception {
    public InvalidArgsException(Exception e) {
        super(e);
    }

    public InvalidArgsException(String message, Exception e) {
        super(message, e);
    }

    public InvalidArgsException(String message) {
        super(message);
    }
}
