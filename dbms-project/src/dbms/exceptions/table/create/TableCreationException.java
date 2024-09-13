package dbms.exceptions.table.create;

import dbms.exceptions.InvalidOperationException;

public class TableCreationException extends InvalidOperationException {
    public TableCreationException(Exception e) {
        super(e);
    }

    public TableCreationException(String message, Exception e) {
        super(message, e);
    }

    public TableCreationException(String message) {
        super(message);
    }
}
