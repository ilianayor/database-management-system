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

public class SelectValidator {

    public static void validateSelect(String args) throws InvalidArgsException {
        String[] parts = StringUtils.split(args, ' ');

        if (parts[0].equals(Keyword.STAR.getValue())) {
            validateSelectAll(parts);
        } else {
            if (parts[0].equals(Keyword.DISTINCT.getValue())) {
                parts = StringUtils.collectFromIndex(parts, 1);
            }

            validateSelectSpecificColumns(parts);
        }
    }

    private static void validateSelectAll(String[] parts) throws InvalidArgsException {
        validateFromKeyword(parts[1]);
        validateTableName(parts[2]);

        if (parts.length >= 4) {
            validateWhereKeyword(parts[3]);
            validateWhereClause(parts[4], parts[2]);
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

    private static void validateTableName(String tableName) throws InvalidArgsException {
        if (!FileSystemExecutor.existFileWithName(tableName)) {
            throw new InvalidArgsException("table does not exist");
        }
    }

    private static void validateFromKeyword(String str) throws InvalidArgsException {
        if (!str.equals(Keyword.FROM.getValue())) {
            throw new InvalidArgsException("expected from keyword");
        }
    }

    private static void validateSelectSpecificColumns(String[] parts) throws InvalidArgsException {
        validateFromKeyword(parts[1]);
        validateTableName(parts[2]);
        validateColumns(StringUtils.split(parts[0], ","), parts[2]);

        if (parts.length >= 4) {
            validateWhereKeyword(parts[3]);
            validateWhereClause(parts[4], parts[2]);
        }
    }

    private static void validateColumns(String[] cols, String tableName) throws InvalidArgsException {
        try {
            String tableMetadata = MetadataHandler.extractTableMetadata(tableName);
            int indexOfOpeningBracket = StringUtils.indexOf(tableMetadata, '(');
            int indexOfClosingBracket = StringUtils.indexOf(tableMetadata, ')');
            Column[] columns = Extractor.extractColumns(StringUtils.substring(tableMetadata, indexOfOpeningBracket + 1, indexOfClosingBracket));

            for (String col : cols) {
                boolean found = false;

                for (Column column : columns) {
                    if (column.getName().equals(col)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    throw new InvalidArgsException("col: " + col + " does not exist");
                }
            }

            for(int i = 0; i < cols.length - 1; i++) {
                for (int j = i + 1; j < cols.length; j++) {
                    if(cols[i].equals(cols[j])){
                        throw new InvalidArgsException("duplicate col: " + cols[i] + " in select");
                    }
                }
            }
        } catch (Exception e) {
            throw new InvalidArgsException(e.getMessage(), e);
        }


    }
}
