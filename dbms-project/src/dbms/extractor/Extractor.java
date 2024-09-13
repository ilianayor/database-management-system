package dbms.extractor;

import dbms.SupportedType;
import dbms.clause.Clause;
import dbms.executor.table.Column;
import dbms.strings.StringUtils;
import dbms.validator.table.TableValidator;

import java.util.Arrays;

public class Extractor {
    public static Column[] extractColumns(String str) {
        str = extractBetweenBrackets(str);
        String[] columnsAsString = StringUtils.split(str, ',');
        Column[] columnsResult = new Column[columnsAsString.length];

        for (int i = 0; i < columnsAsString.length; i++) {
            String[] columnParts = StringUtils.split(columnsAsString[i], ' ');

            String[] columnPair = StringUtils.split(columnParts[0], ':');
            SupportedType columnType = SupportedType.toDefaultType(columnPair[1]);
            String columnName = columnPair[0];

            if (columnParts.length == TableValidator.VALID_COLUMN_NUMBER_ARGS_NO_DEFAULT) {
                columnsResult[i] = new Column(columnType, columnName);
            } else {
                String defaultValue = columnParts[2];
                columnsResult[i] = new Column(columnType, columnName, defaultValue);
            }
        }

        return columnsResult;
    }

    public static String[] extractArgs(String str) {
        int indexOfOpeningBracket = StringUtils.indexOf(str, '(');
        int indexOfClosingBracket = StringUtils.indexOf(str, ')');

        if (indexOfOpeningBracket == indexOfClosingBracket - 1) {
            return new String[]{};
        }

        String argsStr = StringUtils.substring(str, indexOfOpeningBracket + 1, indexOfClosingBracket);
        return StringUtils.split(argsStr, ',');
    }

    public static String extractBetweenBrackets(String str) {
        int indexOfOpeningBracket = StringUtils.indexOf(str, '(');
        int indexOfClosingBracket = StringUtils.indexOf(str, ')');

        if (indexOfOpeningBracket == -1 || indexOfClosingBracket == -1) {
            return str;
        }

        if (indexOfOpeningBracket == indexOfClosingBracket - 1) {
            return "";
        }

        return StringUtils.substring(str, indexOfOpeningBracket + 1, indexOfClosingBracket);
    }

    public static Clause extractClause(String clause) {
        boolean shouldNegate = StringUtils.startsWith(clause, "not");
        String[] clauseParts;

        if (shouldNegate) {
            clauseParts = extractArgs(clause);
            clauseParts = StringUtils.split(clauseParts[0], '=');
        } else {
            clauseParts = StringUtils.split(clause, '=');
        }

        return Clause.build(clauseParts[0], clauseParts[1], shouldNegate);
    }
}

