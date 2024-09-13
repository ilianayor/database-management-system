package dbms.exceptions.table.drop;

import dbms.exceptions.InvalidArgsException;

public class InvalidDropTableArgsException extends InvalidArgsException {
    public InvalidDropTableArgsException(Exception e) {
        super(e);
    }

    public InvalidDropTableArgsException(String message, Exception e) {
        super(message, e);
    }

    public InvalidDropTableArgsException(String message) {
        super(message);
    }
}
