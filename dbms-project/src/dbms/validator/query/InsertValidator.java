package dbms.validator.query;

import dbms.exceptions.InvalidArgsException;
import dbms.executor.filesystem.FileSystemExecutor;
import dbms.executor.metadata.MetadataHandler;
import dbms.executor.query.DataPair;
import dbms.executor.query.Query;
import dbms.extractor.Extractor;
import dbms.keyword.Keyword;
import dbms.strings.StringUtils;

public class InsertValidator {
    private static final int VALID_NUMBER_OF_SPACES = 2;
    private static final String VALUES_PREFIX = "values";

    public static void validateInsert(String args) throws InvalidArgsException {
        String[] parts = StringUtils.split(args, ' ');

        validateNumberOfSpaces(parts.length - 1);
        validateIntoKeyword(parts[0]);
        validateTableExists(parts[1]);
        validateValuesPrefix(parts[2]);
        validateDataTypes(parts[2], parts[1]);
    }

    private static void validateNumberOfSpaces(int numberOfSpaces) throws InvalidArgsException {
        if (numberOfSpaces != VALID_NUMBER_OF_SPACES) {
            throw new InvalidArgsException("invalid args for insert");
        }
    }

    private static void validateIntoKeyword(String str) throws InvalidArgsException {
        if (!str.equals(Keyword.INTO.getValue())) {
            throw new InvalidArgsException("expected into");
        }
    }

    private static void validateTableExists(String tableName) throws InvalidArgsException {
        if (!FileSystemExecutor.existFileWithName(tableName)) {
            throw new InvalidArgsException("table [" + tableName + "] does not exist");
        }
    }

    private static void validateValuesPrefix(String str) throws InvalidArgsException {
        if (!StringUtils.startsWith(str, VALUES_PREFIX)) {
            throw new InvalidArgsException("expected values keyword");
        }
    }

    private static void validateDataTypes(String query, String tableName) throws InvalidArgsException {
        String[] args = Extractor.extractArgs(query);

        try {
            String tableMetadata = MetadataHandler.extractTableMetadata(tableName);
            DataPair[] dataPairs = DataPair.calculate(args);

            if (!Query.match(dataPairs, tableMetadata)) {
                throw new InvalidArgsException("data type mismatch");
            }
        } catch (Exception e) {
            throw new InvalidArgsException("operation failed in data types validation");
        }
    }
}
