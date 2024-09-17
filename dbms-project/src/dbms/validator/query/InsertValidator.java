package dbms.validator.query;

import dbms.Printer;
import dbms.exceptions.InvalidArgsException;
import dbms.executor.filesystem.FileSystemExecutor;
import dbms.executor.metadata.MetadataHandler;
import dbms.executor.query.DataPair;
import dbms.executor.query.Query;
import dbms.extractor.Extractor;
import dbms.keyword.Keyword;
import dbms.strings.StringUtils;

public class InsertValidator {
    private static final int VALID_NUMBER_OF_SPACES = 1;
    private static final String VALUES_PREFIX = "values";

    public static void validateInsert(String args) throws InvalidArgsException {
        String[] parts = StringUtils.split(args, ' ');

        validateNumberOfSpaces(extractNumberOfSpaces(args));
        validateIntoKeyword(parts[0]);
        validateTableExists(parts[1]);
        validateValuesPrefix(parts[2]);

        int indexOfValuesKeyword = StringUtils.indexOf(args, VALUES_PREFIX);
        String query = StringUtils.substring(args, indexOfValuesKeyword + VALUES_PREFIX.length());
        validateDataTypes(query, parts[1]);
    }

    private static int extractNumberOfSpaces(String args) {
        int indexOfValuesKeyword = StringUtils.indexOf(args, VALUES_PREFIX);
        String substr = StringUtils.substring(args, 0, indexOfValuesKeyword - 1);
        return StringUtils.split(substr, ' ').length - 1;
    }

    private static void validateNumberOfSpaces(int numberOfSpaces) throws InvalidArgsException {
        if (numberOfSpaces != VALID_NUMBER_OF_SPACES) {
            throw new InvalidArgsException("Invalid args for insert.");
        }
    }

    private static void validateIntoKeyword(String str) throws InvalidArgsException {
        if (!str.equals(Keyword.INTO.getValue())) {
            throw new InvalidArgsException("Expected into keyword.");
        }
    }

    private static void validateTableExists(String tableName) throws InvalidArgsException {
        if (!FileSystemExecutor.existsFileWithName(tableName)) {
            throw new InvalidArgsException("Table [" + tableName + "] does not exist.");
        }
    }

    private static void validateValuesPrefix(String str) throws InvalidArgsException {
        if (!StringUtils.startsWith(str, VALUES_PREFIX)) {
            throw new InvalidArgsException("Expected values keyword.");
        }
    }

    private static void validateDataTypes(String query, String tableName) throws InvalidArgsException {
        String[] args = Extractor.extractArgs(query);

        try {
            String tableMetadata = MetadataHandler.extractTableMetadata(tableName);
            DataPair[] dataPairs = DataPair.calculate(args);

            if (!Query.match(dataPairs, tableMetadata)) {
                throw new InvalidArgsException("Data type mismatch.");
            }
        } catch (Exception e) {
            throw new InvalidArgsException("Operation failed in data types validation.");
        }
    }
}
