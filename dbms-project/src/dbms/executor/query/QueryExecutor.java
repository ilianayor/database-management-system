package dbms.executor.query;

import dbms.exceptions.InvalidOperationException;
import dbms.executor.filesystem.FileSystemExecutor;

public class QueryExecutor {
    public void insert(String tableName, DataPair[] dataPairs, String[] defaultValues) throws InvalidOperationException {
        FileSystemExecutor.append(tableName, concatenateInsert(dataPairs, defaultValues));
    }

    private String concatenateInsert(DataPair[] dataPairs, String[] defaultValues) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < dataPairs.length; i++) {
            sb.append(dataPairs[i].getValue());
            if (i != dataPairs.length - 1) {
                sb.append(",");
            }
        }

        for (int i = 0; i < defaultValues.length; i++) {
            if (i == 0) {
                sb.append(",");
            }
            sb.append(defaultValues[i]);
            if (i != defaultValues.length - 1) {
                sb.append(",");
            }
        }

        return sb + System.lineSeparator();
    }
}
