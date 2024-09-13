package dbms.executor.filesystem;

import dbms.exceptions.InvalidOperationException;
import dbms.exceptions.table.create.TableCreationException;
import dbms.executor.metadata.MetadataHandler;
import dbms.strings.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemExecutor {
    public static void createFile(String name) throws TableCreationException {
        try {
            Files.createFile(Path.of(name));
        } catch (Exception e) {
            throw new TableCreationException("fail creation failed for table: [" + name + "]");
        }
    }

    public static void deleteFile(String name) throws TableCreationException {
        try {
            Files.delete(Path.of(name));
        } catch (Exception e) {
            throw new TableCreationException("fail creation failed for table: [" + name + "]");
        }
    }

    public static void append(String fileName, String str) throws InvalidOperationException {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(str);
        } catch (Exception e) {
            throw new InvalidOperationException("failed to write to metadata file");
        }
    }

    public static void clear(String fileName) throws InvalidOperationException {
        try (FileWriter writer = new FileWriter(fileName, false)) {
            writer.write("");
        } catch (Exception e) {
            throw new InvalidOperationException("failed clear file");
        }
    }

    public static long getFileSize(String fileName) throws InvalidOperationException {
        try {
            return Files.size(Path.of(fileName));
        } catch (Exception e) {
            throw new InvalidOperationException("failed to get file size");
        }
    }

    public static boolean existFileWithName(String name) {
        return Files.exists(Path.of(name));
    }

    public static int countNumberOfLines(String fileName) throws InvalidOperationException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(String.valueOf(Path.of(fileName))))) {
            int lines = 0;
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                if (!line.isEmpty()) {
                    lines++;
                }
            }

            return lines;
        } catch (Exception e) {
            throw new InvalidOperationException("update metadata failed", e);
        }
    }

    public static String[] loadInMemory(String fileName) throws InvalidOperationException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(String.valueOf(Path.of(fileName))))) {
            String[] lines =  new String[countNumberOfLines(fileName)];
            int index = 0;
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (!line.isEmpty()) {
                    lines[index++] = line;
                }
            }

            return lines;
        } catch (Exception e) {
            throw new InvalidOperationException("update metadata failed", e);
        }
    }
}
