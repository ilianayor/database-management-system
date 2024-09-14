package dbms.validator.table;

import dbms.SupportedType;
import dbms.exceptions.InvalidArgsException;
import dbms.exceptions.table.create.InvalidCreateTableArgsException;
import dbms.exceptions.table.drop.InvalidDropTableArgsException;
import dbms.executor.table.Column;
import dbms.extractor.Extractor;
import dbms.strings.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TableValidator {
    public static final int VALID_COLUMN_NUMBER_ARGS_NO_DEFAULT = 1;
    public static final int VALID_COLUMN_NUMBER_ARGS_WITH_DEFAULT = 3;
    public static final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy";

    public static void validateCreateTableArgs(String args) throws InvalidArgsException {
        validateArgsNotNull(args);
        validateCreateTableOpeningBracket(args);
        validateCreateTableClosingBracket(args);
        int openingBracketIndex = StringUtils.indexOf(args, '(');
        int closingBracketIndex = StringUtils.indexOf(args, ')');

        String tableName = StringUtils.substring(args, 0, openingBracketIndex);
        validateTableName(tableName);

        String columns = args.substring(openingBracketIndex + 1, closingBracketIndex);
        validateColumns(columns);
        validateColNameUniqueness(Extractor.extractColumns(columns));
    }

    private static void validateArgsNotNull(String args) throws InvalidArgsException {
        if (args == null) {
            throw new InvalidArgsException("no args provided");
        }
    }

    private static void validateColumns(String str) throws InvalidCreateTableArgsException {
        if (str.isEmpty() || str.isBlank()) {
            throw new InvalidCreateTableArgsException("missing column info");
        }

        String[] columnsAsString = StringUtils.split(str, ',');

        for (int i = 0; i < columnsAsString.length; i++) {
            String[] columnParts = StringUtils.split(columnsAsString[i], ' ');

            if (columnParts.length != VALID_COLUMN_NUMBER_ARGS_NO_DEFAULT
                    && columnParts.length != VALID_COLUMN_NUMBER_ARGS_WITH_DEFAULT) {
                throw new InvalidCreateTableArgsException("invalid number of column parts: [" + columnsAsString[i] + "]");
            }

            if (columnParts.length == VALID_COLUMN_NUMBER_ARGS_NO_DEFAULT) {
                if (StringUtils.indexOf(columnParts[0], ':') == -1) {
                    throw new InvalidCreateTableArgsException("missing \":\" in [" + columnsAsString[i] + "]");
                }

                String[] columnPair = StringUtils.split(columnParts[0], ':');
                if (columnPair[0].isEmpty() || columnPair[1].isEmpty()) {
                    throw new InvalidCreateTableArgsException("missing column type or name in [" + columnsAsString[i] + "]");
                }
            } else {
                if (StringUtils.indexOf(columnParts[0], ':') == -1) {
                    throw new InvalidCreateTableArgsException("missing \":\" in [" + columnsAsString[i] + "]");
                }

                String[] columnPair = StringUtils.split(columnParts[0], ':');
                if (columnPair[0].isEmpty() || columnPair[1].isEmpty()) {
                    throw new InvalidCreateTableArgsException("missing column type or name in [" + columnsAsString[i] + "]");
                }

                if (!columnParts[1].equals("default")) {
                    throw new InvalidCreateTableArgsException("missing 'default' in [" + columnsAsString[i] + "]");
                }

                validateDefaultValueType(columnPair[1], columnParts[2], columnsAsString[i]);
            }
        }
    }

    private static void validateColNameUniqueness(Column[] columns) throws InvalidCreateTableArgsException {
        for (int i = 0; i < columns.length - 1; i++) {
            String ithColumnName = columns[i].getName();
            for (int j = i + 1; j < columns.length; j++) {
                String jthColumnName = columns[j].getName();
                if (ithColumnName.equals(jthColumnName)) {
                    throw new InvalidCreateTableArgsException("duplicate column name: [" + ithColumnName + "]");
                }
            }
        }
    }

    private static void validateDefaultValueType(String columnType, String defaultValue, String line) throws InvalidCreateTableArgsException {
        try {
            SupportedType supportedType = SupportedType.toSupportedType(columnType);

            if (supportedType == SupportedType.UNKNOWN) {
                throw new InvalidCreateTableArgsException("invalid default type: [" + line + "]");
            }

            if (columnType.equals(SupportedType.INTEGER.toString())) {
                if (!SupportedType.isInteger(defaultValue)) {
                    throw new InvalidCreateTableArgsException("invalid default type: [" + line + "]");
                }
            } else if (columnType.equals(SupportedType.DATE.toString())) {
                if (!SupportedType.isDate(defaultValue)) {
                    throw new InvalidCreateTableArgsException("invalid default type: [" + line + "]");
                }
            } else if (columnType.equals(SupportedType.STRING.toString())) {
                if (!SupportedType.isString(defaultValue)) {
                    throw new InvalidCreateTableArgsException("invalid default type: [" + line + "]");
                }
            }
        } catch (Exception e) {
            throw new InvalidCreateTableArgsException("invalid default type: [" + line + "]");
        }
    }

    private static void validateTableName(String tableName) throws InvalidCreateTableArgsException {
        if (tableName.isBlank() || tableName.isEmpty()) {
            throw new InvalidCreateTableArgsException("no table name given");
        }
    }

    private static void validateCreateTableOpeningBracket(String args) throws InvalidCreateTableArgsException {
        int indexOfOpeningBracket = args.indexOf('(');

        if (indexOfOpeningBracket == -1) {
            throw new InvalidCreateTableArgsException("no opening bracket");
        }
    }

    private static void validateCreateTableClosingBracket(String args) throws InvalidCreateTableArgsException {
        int indexOfClosingBracket = args.indexOf(')');

        if (indexOfClosingBracket == -1) {
            throw new InvalidCreateTableArgsException("no closing bracket");
        }
    }

    public static void validateDropTableArgs(String args) throws InvalidArgsException {
        validateArgsNotNull(args);

        String[] parts = StringUtils.split(args, ' ');

        if (parts.length != 1) {
            throw new InvalidDropTableArgsException("expected only 1 arg(table name)");
        }
    }

    public static void validateListTables(String args) throws InvalidArgsException {
        if (args != null) {
            throw new InvalidArgsException("args not expected");
        }
    }

    public static void validateTableInfo(String args) throws InvalidArgsException {
        validateArgsNotNull(args);

        String[] parts = StringUtils.split(args, ' ');

        if (parts.length != 1) {
            throw new InvalidDropTableArgsException("expected only 1 arg(table name)");
        }
    }
}
