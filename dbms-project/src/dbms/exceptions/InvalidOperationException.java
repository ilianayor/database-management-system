package dbms.exceptions;

public class InvalidOperationException extends Exception {
    public InvalidOperationException(Exception e) {
        super(e);
    }

    public InvalidOperationException(String message, Exception e) {
        super(message, e);
    }

    public InvalidOperationException(String message) {
        super(message);
    }
}
