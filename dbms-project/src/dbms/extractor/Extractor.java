package dbms.extractor;

import dbms.Printer;
import dbms.SupportedType;
import dbms.clause.Clause;
import dbms.clause.Sign;
import dbms.clause.TypeValuePair;
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
            SupportedType columnType = SupportedType.toSupportedType(columnPair[1]);
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
        if (StringUtils.startsWith(clause, "not")) {
            String[] clauseParts = extractArgs(clause);
            return extractClauseImpl(clauseParts[0], true);
        }

        return extractClauseImpl(clause, false);
    }

    private static Clause extractClauseImpl(String str, boolean shouldNegate) {
        String[] parts = new String[]{};
        Sign sign = Sign.UNKNOWN;

        if (StringUtils.contains(str, Sign.LESS_THAN_OR_EQUALS.getValue())) {
            sign = Sign.LESS_THAN_OR_EQUALS;
            parts = StringUtils.split(str, Sign.LESS_THAN_OR_EQUALS.getValue());
        } else if (StringUtils.contains(str, Sign.GREATER_THAN_OR_EQUALS.getValue())) {
            sign = Sign.GREATER_THAN_OR_EQUALS;
            parts = StringUtils.split(str, Sign.GREATER_THAN_OR_EQUALS.getValue());
        } else if (StringUtils.contains(str, Sign.NOT_EQUALS.getValue())) {
            sign = Sign.NOT_EQUALS;
            parts = StringUtils.split(str, Sign.NOT_EQUALS.getValue());
        } else if (StringUtils.contains(str, Sign.EQUALS.getValue())) {
            sign = Sign.EQUALS;
            parts = StringUtils.split(str, Sign.EQUALS.getValue());
        } else if (StringUtils.contains(str, Sign.LESS_THAN.getValue())) {
            sign = Sign.LESS_THAN;
            parts = StringUtils.split(str, Sign.LESS_THAN.getValue());
        } else if (StringUtils.contains(str, Sign.GREATER_THAN.getValue())) {
            sign = Sign.GREATER_THAN;
            parts = StringUtils.split(str, Sign.GREATER_THAN.getValue());
        }

        if (shouldNegate) {
            sign = negateSign(sign);
        }

        if (sign == Sign.UNKNOWN) {
            return Clause.empty();
        }

        TypeValuePair typeValuePair = TypeValuePair.of(parts[0], parts[1], SupportedType.getSupportedType(parts[1]));
        return Clause.build(typeValuePair, sign);
    }

    private static Sign negateSign(Sign sign) {
        if (sign == Sign.EQUALS) {
            sign = Sign.NOT_EQUALS;
        } else if (sign == Sign.NOT_EQUALS) {
            sign = Sign.EQUALS;
        } else if (sign == Sign.LESS_THAN) {
            sign = Sign.GREATER_THAN_OR_EQUALS;
        } else if (sign == Sign.GREATER_THAN_OR_EQUALS) {
            sign = Sign.LESS_THAN;
        } else if (sign == Sign.GREATER_THAN) {
            sign = Sign.LESS_THAN_OR_EQUALS;
        } else if (sign == Sign.LESS_THAN_OR_EQUALS) {
            sign = Sign.GREATER_THAN;
        }

        return sign;
    }
}

