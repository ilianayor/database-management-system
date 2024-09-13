package dbms.exceptions.table.create;

import dbms.exceptions.InvalidArgsException;

public class InvalidCreateTableArgsException extends InvalidArgsException {
    public InvalidCreateTableArgsException(Exception e) {
        super(e);
    }

    public InvalidCreateTableArgsException(String message, Exception e) {
        super(message, e);
    }

    public InvalidCreateTableArgsException(String message) {
        super(message);
    }
}
