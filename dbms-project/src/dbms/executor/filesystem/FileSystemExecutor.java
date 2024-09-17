package dbms.executor.filesystem;

import dbms.exceptions.InvalidOperationException;
import dbms.exceptions.table.create.TableCreationException;


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
            throw new TableCreationException("File creation failed for table: [" + name + "].", e);
        }
    }

    public static void deleteFile(String name) throws TableCreationException {
        try {
            Files.delete(Path.of(name));
        } catch (Exception e) {
            throw new TableCreationException("File deletion failed for table: [" + name + "].", e);
        }
    }

    public static void append(String fileName, String str) throws InvalidOperationException {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(str);
        } catch (Exception e) {
            throw new InvalidOperationException("Failed to append data to the file: " + fileName, e);
        }
    }

    public static void replaceFileContents(String fileName, String[] lines) throws InvalidOperationException {
        try (FileWriter writer = new FileWriter(fileName, false)) {
            for (String line : lines) {
                writer.write(line);
                writer.write(System.lineSeparator());
            }
        } catch (Exception e) {
            throw new InvalidOperationException("Unable to replace contents of file: " + fileName, e);
        }
    }

    public static void clear(String fileName) throws InvalidOperationException {
        try (FileWriter writer = new FileWriter(fileName, false)) {
            writer.write("");
        } catch (Exception e) {
            throw new InvalidOperationException("Failed to clear file: " + fileName, e);
        }
    }

    public static long getFileSize(String fileName) throws InvalidOperationException {
        try {
            return Files.size(Path.of(fileName));
        } catch (Exception e) {
            throw new InvalidOperationException("Failed to get file size for file: " + fileName, e);
        }
    }

    public static boolean existsFileWithName(String name) {
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
            throw new InvalidOperationException("Failed to count lines in file: " + fileName, e);
        }
    }

    public static String[] loadInMemory(String fileName) throws InvalidOperationException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(String.valueOf(Path.of(fileName))))) {
            String[] lines = new String[countNumberOfLines(fileName)];
            int index = 0;
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (!line.isEmpty()) {
                    lines[index++] = line;
                }
            }
            return lines;
        } catch (Exception e) {
            throw new InvalidOperationException("Failed to load file into memory: " + fileName, e);
        }
    }
}
