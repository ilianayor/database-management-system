package dbms.executor.table;

import dbms.extractor.Extractor;
import dbms.strings.StringUtils;

public class Table {
    private final String name;
    private final Column[] columns;

    public Table(String name, Column[] columns) {
        this.name = name;
        this.columns = columns;
    }

    public static Table of(String line) {
        int indexOfOpeningBracket = StringUtils.indexOf(line, '(');
        int indexOfClosingBracket = StringUtils.indexOf(line, ')');
        String tableName = StringUtils.substring(line, 0, indexOfOpeningBracket);
        Column[] columns = Extractor.extractColumns(StringUtils.substring(line, indexOfOpeningBracket + 1, indexOfClosingBracket));

        return new Table(tableName, columns);
    }

    public String getName() {
        return name;
    }

    public Column[] getColumns() {
        return columns;
    }
}
