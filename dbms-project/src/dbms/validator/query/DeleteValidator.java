package dbms.validator.query;

import dbms.SupportedType;
import dbms.clause.Clause;
import dbms.exceptions.InvalidArgsException;
import dbms.executor.filesystem.FileSystemExecutor;
import dbms.executor.metadata.MetadataHandler;
import dbms.executor.table.Column;
import dbms.extractor.Extractor;
import dbms.strings.StringUtils;

public class DeleteValidator {
    private static final String FROM_KEYWORD = "from";
    private static final String WHERE_KEYWORD = "where";
    private static final char EQUALS_SIGN = '=';

    public static void validateDelete(String args) throws InvalidArgsException {
        String[] parts = StringUtils.split(args, ' ');
        validateNumberOfArgs(parts);
        validateFromKeyword(parts[0]);
        validateTableName(parts[1]);

        if (parts.length >= 3) {
            validateWhereKeyword(parts[2]);
            validateWhereClause(parts[3], parts[1]);
        }
    }

    private static void validateNumberOfArgs(String[] parts) throws InvalidArgsException {
        if (parts.length % 2 != 0) {
            throw new InvalidArgsException("Invalid number of arguments.");
        }
    }

    private static void validateFromKeyword(String str) throws InvalidArgsException {
        if (!str.equals(FROM_KEYWORD)) {
            throw new InvalidArgsException("expected from keyword");
        }
    }

    private static void validateTableName(String tableName) throws InvalidArgsException {
        if (!FileSystemExecutor.existFileWithName(tableName)) {
            throw new InvalidArgsException("table does not exist");
        }
    }

    private static void validateWhereKeyword(String str) throws InvalidArgsException {
        if (!str.equals(WHERE_KEYWORD)) {
            throw new InvalidArgsException("expected from keyword");
        }
    }

    private static void validateWhereClause(String clauseStr, String tableName) throws InvalidArgsException {
        if (StringUtils.indexOf(clauseStr, EQUALS_SIGN) == -1) {
            throw new InvalidArgsException("expected '=' sign in clause");
        }

        Clause clause = Extractor.extractClause(clauseStr);
        String tableMetadata;
        try {
            tableMetadata = MetadataHandler.extractTableMetadata(tableName);
        } catch (Exception e) {
            throw new InvalidArgsException("validation failed", e);
        }

        Column[] columns = Extractor.extractColumns(tableMetadata);

        boolean found = false;

        for (Column c : columns) {
            if (c.getName().equals(clause.getColName())) {
                if (!SupportedType.isOfType(c.getSupportedType(), clause.getValue())) {
                    throw new InvalidArgsException("invalid clause value type");
                } else {
                    found = true;
                }
                break;
            }
        }

        if (!found) {
            throw new InvalidArgsException("column not found in metadata");
        }
    }
}
