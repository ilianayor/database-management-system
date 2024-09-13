package dbms.executor.table;

import dbms.exceptions.InvalidOperationException;
import dbms.executor.filesystem.FileSystemExecutor;

import java.io.File;

public class TableExecutor {
    public void createTable(Table table) throws InvalidOperationException {
        String tableName = table.getName();

        if (FileSystemExecutor.existFileWithName(tableName)) {
            throw new InvalidOperationException("table with name: [" + tableName + "] already exists");
        }

        FileSystemExecutor.createFile(tableName);
    }

    public void dropTable(String tableName) throws InvalidOperationException {
        if (!FileSystemExecutor.existFileWithName(tableName)) {
            throw new InvalidOperationException("table with name: [" + tableName + "] does not exist");
        }

        FileSystemExecutor.deleteFile(tableName);
    }
}
