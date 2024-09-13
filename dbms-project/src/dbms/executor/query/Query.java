package dbms.executor.query;

import dbms.exceptions.InvalidArgsException;
import dbms.executor.table.Column;
import dbms.extractor.Extractor;
import dbms.strings.StringUtils;

public class Query {
    private final String tableName;
    private DataPair[] dataPairs;

    public Query(String tableName, DataPair[] dataPairs) {
        this.tableName = tableName;
        this.dataPairs = dataPairs;
    }

    public static boolean match(DataPair[] dataPairs, String tableMetadata) {
        try {
            int indexOfOpeningBracket = StringUtils.indexOf(tableMetadata, '(');
            int indexOfClosingBracket = StringUtils.indexOf(tableMetadata, ')');
            Column[] columns = Extractor.extractColumns(StringUtils.substring(tableMetadata, indexOfOpeningBracket + 1, indexOfClosingBracket));

            if (dataPairs.length > columns.length) {
                return false;
            }

            for (int i = 0; i < dataPairs.length; i++) {
                if (dataPairs[i].getSupportedType() != columns[i].getSupportedType()) {
                    return false;
                }
            }

            for (int i = dataPairs.length; i < columns.length; i++) {
                if (columns[i].getDefaultValue().isBlank()) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getTableName() {
        return tableName;
    }

    public DataPair[] getDataPairs() {
        return dataPairs;
    }
}
