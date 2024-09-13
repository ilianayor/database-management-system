package dbms.validator.query;

import dbms.exceptions.InvalidArgsException;

public class DeleteValidator {
    public static void validateDelete(String args) throws InvalidArgsException {
        System.out.println("shte validirame " + args);
    }
}
