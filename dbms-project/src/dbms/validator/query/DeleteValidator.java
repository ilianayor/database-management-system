package dbms.validator.query;

import dbms.SupportedType;
import dbms.clause.Clause;
import dbms.clause.Sign;
import dbms.exceptions.InvalidArgsException;
import dbms.executor.filesystem.FileSystemExecutor;
import dbms.executor.metadata.MetadataHandler;
import dbms.executor.table.Column;
import dbms.extractor.Extractor;
import dbms.keyword.Keyword;
import dbms.strings.StringUtils;

public class DeleteValidator {
    private static final int VALID_NUMBER_OF_SPACE_NO_WHERE_KEYWORD = 1;
    private static final int VALID_NUMBER_OF_SPACE_WITH_WHERE_KEYWORD = 3;

    public static void validateDelete(String args) throws InvalidArgsException {
        String[] parts = StringUtils.split(args, ' ');

        validateNumberOfArgs(args);
        validateFromKeyword(parts[0]);
        validateTableName(parts[1]);

        if (parts.length >= 3) {
            String substringAfterWhere = StringUtils.extractSubstringAfter(args, Keyword.WHERE.getValue());
            validateWhereClause(substringAfterWhere, parts[1]);
        }
    }

    private static void validateNumberOfArgs(String args) throws InvalidArgsException {
        String[] splitted;

        if (!StringUtils.contains(args, Keyword.WHERE.getValue())) {
            splitted = StringUtils.split(args, ' ');

            if (splitted.length - 1 != VALID_NUMBER_OF_SPACE_NO_WHERE_KEYWORD) {
                throw new InvalidArgsException("Invalid number of arguments.");
            }

            return;
        }

        int indexOfWhereKeyword = StringUtils.indexOf(args, Keyword.WHERE.getValue());
        int offset = indexOfWhereKeyword + Keyword.WHERE.getValue().length();
        String substr = StringUtils.substring(args, offset);
        int indexOfFirstNonEmptyLetter = StringUtils.extractIndexOfFirstNonEmptyLetter(substr);
        String toValidateNumberOfArgs = StringUtils.substring(args, 0, offset + indexOfFirstNonEmptyLetter + 1);
        splitted = StringUtils.split(toValidateNumberOfArgs, ' ');

        if (splitted.length - 1 != VALID_NUMBER_OF_SPACE_WITH_WHERE_KEYWORD) {
            throw new InvalidArgsException("Invalid number of arguments.");
        }
    }

    private static void validateFromKeyword(String str) throws InvalidArgsException {
        if (!str.equals(Keyword.FROM.getValue())) {
            throw new InvalidArgsException("Expected from keyword.");
        }
    }

    private static void validateTableName(String tableName) throws InvalidArgsException {
        if (!FileSystemExecutor.existsFileWithName(tableName)) {
            throw new InvalidArgsException("Table does not exist.");
        }
    }

    private static void validateWhereKeyword(String str) throws InvalidArgsException {
        if (!str.equals(Keyword.WHERE.getValue())) {
            throw new InvalidArgsException("Expected where keyword.");
        }
    }

    private static void validateWhereClause(String clauseStr, String tableName) throws InvalidArgsException {
        Clause clause = Extractor.extractClause(clauseStr);

        if (clause.getSign() == Sign.UNKNOWN) {
            throw new InvalidArgsException("Sign in where clause is unknown.");
        }

        if (clause.getTypeValuePair().getSupportedType() == SupportedType.UNKNOWN) {
            throw new InvalidArgsException("Unsupported value type.");
        }

        String tableMetadata;
        try {
            tableMetadata = MetadataHandler.extractTableMetadata(tableName);
        } catch (Exception e) {
            throw new InvalidArgsException("Validation failed.", e);
        }

        Column[] columns = Extractor.extractColumns(tableMetadata);

        boolean found = false;

        for (Column c : columns) {
            if (c.getName().equals(clause.getTypeValuePair().getColName())) {
                if (!SupportedType.isOfType(c.getSupportedType(), clause.getTypeValuePair().getValue())) {
                    throw new InvalidArgsException("Invalid clause value type.");
                } else {
                    found = true;
                }
                break;
            }
        }

        if (!found) {
            throw new InvalidArgsException("Column not found in metadata.");
        }
    }
}
