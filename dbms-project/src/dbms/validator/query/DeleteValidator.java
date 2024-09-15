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
        if (!str.equals(Keyword.FROM.getValue())) {
            throw new InvalidArgsException("expected from keyword");
        }
    }

    private static void validateTableName(String tableName) throws InvalidArgsException {
        if (!FileSystemExecutor.existFileWithName(tableName)) {
            throw new InvalidArgsException("table does not exist");
        }
    }

    private static void validateWhereKeyword(String str) throws InvalidArgsException {
        if (!str.equals(Keyword.WHERE.getValue())) {
            throw new InvalidArgsException("expected where keyword");
        }
    }

    private static void validateWhereClause(String clauseStr, String tableName) throws InvalidArgsException {
        Clause clause = Extractor.extractClause(clauseStr);

        if (clause.getSign() == Sign.UNKNOWN) {
            throw new InvalidArgsException("sign in where clause is unknown");
        }

        if (clause.getTypeValuePair().getSupportedType() == SupportedType.UNKNOWN) {
            throw new InvalidArgsException("unsupported value type");
        }

        String tableMetadata;
        try {
            tableMetadata = MetadataHandler.extractTableMetadata(tableName);
        } catch (Exception e) {
            throw new InvalidArgsException("validation failed", e);
        }

        Column[] columns = Extractor.extractColumns(tableMetadata);

        boolean found = false;

        for (Column c : columns) {
            if (c.getName().equals(clause.getTypeValuePair().getColName())) {
                if (!SupportedType.isOfType(c.getSupportedType(), clause.getTypeValuePair().getValue())) {
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
