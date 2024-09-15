package dbms.validator.query;

import dbms.exceptions.InvalidArgsException;

public class QueryValidator {
    public static void validateInsert(String args) throws InvalidArgsException {
        InsertValidator.validateInsert(args);
    }

    public static void validateDelete(String args) throws InvalidArgsException {
        DeleteValidator.validateDelete(args);
    }

    public static void validateSelect(String args) throws InvalidArgsException {
        SelectValidator.validateSelect(args);
    }
}
