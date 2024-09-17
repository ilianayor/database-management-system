package dbms.validator;

import dbms.command.Command;
import dbms.command.Pair;
import dbms.exceptions.InvalidCommandNameException;
import dbms.exceptions.InvalidArgsException;
import dbms.validator.query.QueryValidator;
import dbms.validator.table.TableValidator;

public class InputValidator {
    public static void validate(Pair pair) throws InvalidCommandNameException, InvalidArgsException {
        validateCommand(pair.getCommandName());
        validateArgs(pair);
    }

    private static void validateCommand(String commandName) throws InvalidCommandNameException {
        Command command = null;

        for (Command cmd : Command.values()) {
            if (cmd.toString().equals(commandName)) {
                command = cmd;
                break;
            }
        }

        if (command == null) {
            throw new InvalidCommandNameException("Command is unknown.");
        }
    }

    private static void validateArgs(Pair pair) throws InvalidArgsException {
        if (pair.getCommandName().equals(Command.CREATE_TABLE.toString())) {
            TableValidator.validateCreateTableArgs(pair.getArgs());
        } else if (pair.getCommandName().equals(Command.DROP_TABLE.toString())) {
            TableValidator.validateDropTableArgs(pair.getArgs());
        } else if (pair.getCommandName().equals(Command.LIST_TABLES.toString())) {
            TableValidator.validateListTables(pair.getArgs());
        } else if (pair.getCommandName().equals(Command.TABLE_INFO.toString())) {
            TableValidator.validateTableInfo(pair.getArgs());
        } else if (pair.getCommandName().equals(Command.INSERT.toString())) {
            QueryValidator.validateInsert(pair.getArgs());
        } else if (pair.getCommandName().equals(Command.DELETE.toString())) {
            QueryValidator.validateDelete(pair.getArgs());
        } else if (pair.getCommandName().equals(Command.SELECT.toString())) {
            QueryValidator.validateSelect(pair.getArgs());
        }
    }
}
