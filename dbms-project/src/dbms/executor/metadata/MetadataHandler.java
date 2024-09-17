package dbms.executor.metadata;

import dbms.exceptions.InvalidOperationException;
import dbms.exceptions.table.create.TableCreationException;
import dbms.executor.filesystem.FileSystemExecutor;
import dbms.executor.table.Column;
import dbms.strings.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;

public class MetadataHandler {
    public static final String METADATA_FILE_NAME = "METADATA_FILE";

    static {
        if (!FileSystemExecutor.existsFileWithName(METADATA_FILE_NAME)) {
            try {
                FileSystemExecutor.createFile(METADATA_FILE_NAME);
            } catch (TableCreationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void updateWithNewTable(String args) throws InvalidOperationException {
        try {
            FileSystemExecutor.append(METADATA_FILE_NAME, args);
            FileSystemExecutor.append(METADATA_FILE_NAME, System.lineSeparator());
        } catch (Exception e) {
            throw new InvalidOperationException("Failed to update metadata file with new table.", e);
        }
    }

    public void updateWithDeletedTable(String tableName) throws InvalidOperationException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(String.valueOf(Path.of(METADATA_FILE_NAME))))) {

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (!line.startsWith(tableName)) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                }
            }

            FileSystemExecutor.clear(METADATA_FILE_NAME);
            FileSystemExecutor.append(METADATA_FILE_NAME, sb.toString());

        } catch (Exception e) {
            throw new InvalidOperationException("Failed to update metadata file by removing table: " + tableName, e);
        }
    }

    public String[] extractMetadataTableNames() throws InvalidOperationException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(String.valueOf(Path.of(METADATA_FILE_NAME))))) {
            String[] result = new String[FileSystemExecutor.countNumberOfLines(METADATA_FILE_NAME)];
            String line;
            int index = 0;

            while ((line = bufferedReader.readLine()) != null) {
                int indexOfOpeningBracket = StringUtils.indexOf(line, '(');
                String tableName = StringUtils.substring(line, 0, indexOfOpeningBracket);
                result[index++] = tableName;
            }

            return result;

        } catch (Exception e) {
            throw new InvalidOperationException("Failed to extract table names from metadata.", e);
        }
    }

    public static String extractTableMetadata(String tableName) throws InvalidOperationException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(String.valueOf(Path.of(METADATA_FILE_NAME))))) {
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                if (StringUtils.startsWith(line, tableName)) {
                    return line;
                }
            }

            return "";
        } catch (Exception e) {
            throw new InvalidOperationException("Failed to extract metadata of table: " + tableName, e);
        }
    }

    public String[] extractDefaultValues(int startIndex, Column[] columns) {
        String[] result = new String[columns.length - startIndex];
        int index = 0;

        for (int i = startIndex; i < columns.length; i++) {
            result[index++] = columns[i].getDefaultValue();
        }

        return result;
    }
}
