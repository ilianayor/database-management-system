package dbms.executor.table;

import dbms.exceptions.InvalidOperationException;
import dbms.executor.filesystem.FileSystemExecutor;

import java.io.File;

public class TableExecutor {
    public void createTable(Table table) throws InvalidOperationException {
        if (table == null) {
            return;
        }

        String tableName = table.getName();
        FileSystemExecutor.createFile(tableName);
    }

    public void dropTable(String tableName) throws InvalidOperationException {
        if (tableName == null) {
            return;
        }

        FileSystemExecutor.deleteFile(tableName);
    }
}
