package dbms.executor;

import dbms.Printer;
import dbms.SuccessMessages;
import dbms.clause.Clause;
import dbms.command.Command;
import dbms.command.CommandParser;
import dbms.command.Pair;
import dbms.exceptions.InvalidOperationException;
import dbms.executor.filesystem.FileSystemExecutor;
import dbms.executor.metadata.MetadataHandler;
import dbms.executor.query.DataPair;
import dbms.executor.query.QueryExecutor;
import dbms.executor.table.Column;
import dbms.executor.table.Table;
import dbms.executor.table.TableExecutor;
import dbms.extractor.Extractor;
import dbms.strings.StringUtils;
import dbms.validator.InputValidator;

import static dbms.SuccessMessages.SUCCESSFUL_ROW_INSERTION;


public class Executor {
    private final CommandParser commandParser;
    private final TableExecutor tableExecutor;
    private final QueryExecutor queryExecutor;
    private final MetadataHandler metadataHandler;

    public Executor() {
        this.commandParser = new CommandParser();
        this.tableExecutor = new TableExecutor();
        this.metadataHandler = new MetadataHandler();
        this.queryExecutor = new QueryExecutor();
    }

    public void execute(String line) {
        Pair pair = this.commandParser.parse(line);

        try {
            InputValidator.validate(pair);
            executeProcess(pair);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void executeProcess(Pair pair) throws InvalidOperationException {
        if (pair.getCommandName().equals(Command.CREATE_TABLE.toString())) {
            executeCreateTable(pair);
        } else if (pair.getCommandName().equals(Command.DROP_TABLE.toString())) {
            executeDropTable(pair);
        } else if (pair.getCommandName().equals(Command.LIST_TABLES.toString())) {
            executeListTables(pair);
        } else if (pair.getCommandName().equals(Command.TABLE_INFO.toString())) {
            executeTableInfo(pair);
        } else if (pair.getCommandName().equals(Command.INSERT.toString())) {
            executeInsert(pair);
        } else if (pair.getCommandName().equals(Command.DELETE.toString())) {
            executeDelete(pair);
        }
    }

    private void executeCreateTable(Pair pair) throws InvalidOperationException {
        Table table = Table.of(pair.getArgs());
        this.tableExecutor.createTable(table);
        this.metadataHandler.updateWithNewTable(pair.getArgs());

        System.out.println(SuccessMessages.SUCCESSFUL_TABLE_CREATION);
    }

    private void executeDropTable(Pair pair) throws InvalidOperationException {
        String tableName = StringUtils.split(pair.getArgs(), ' ')[0];
        this.tableExecutor.dropTable(tableName);
        this.metadataHandler.updateWithDeletedTable(tableName);

        System.out.println(SuccessMessages.SUCCESSFUL_TABLE_DELETION);
    }

    private void executeListTables(Pair pair) throws InvalidOperationException {
        StringBuilder sb = new StringBuilder();
        String[] tables = this.metadataHandler.extractMetadataTableNames();

        for (String table : tables) {
            sb.append(table).append(System.lineSeparator());
        }

        System.out.print(sb);
    }

    private void executeTableInfo(Pair pair) throws InvalidOperationException {
        String[] tables = this.metadataHandler.extractMetadataTableNames();

        for (String table : tables) {
            if (table.equals(pair.getArgs())) {
                long fileSize = FileSystemExecutor.getFileSize(table);
                int records = FileSystemExecutor.countNumberOfLines(table);
                System.out.println(table + ", " + fileSize + ", " + records);
                return;
            }
        }

        System.out.println("the table [" + pair.getArgs() + "] does not exist");
    }

    private void executeInsert(Pair pair) throws InvalidOperationException {
        String[] parts = StringUtils.split(pair.getArgs(), ' ');
        String tableName = parts[1];
        String[] args = Extractor.extractArgs(parts[2]);
        DataPair[] dataPairs = DataPair.calculate(args);
        String tableMetadata = MetadataHandler.extractTableMetadata(tableName);
        Column[] columns = Extractor.extractColumns(tableMetadata);
        String[] defaultValues = metadataHandler.extractDefaultValues(dataPairs.length, columns);

        this.queryExecutor.insert(tableName, dataPairs, defaultValues);
        System.out.println(SUCCESSFUL_ROW_INSERTION);
    }

    private void executeDelete(Pair pair) throws InvalidOperationException {
        String[] p = StringUtils.split(pair.getArgs(), ' ');
        Clause clause = Extractor.extractClause(p[p.length - 1]);
        String tableName = p[1];
        String[] lines = FileSystemExecutor.loadInMemory(tableName);
        Printer.printArr(lines);
//        String[] parts = StringUtils.split(pair.getArgs(), ' ');
//        String tableName = parts[1];
//        String[] args = Extractor.extractArgs(parts[2]);
//        DataPair[] dataPairs = DataPair.calculate(args);
//        String tableMetadata = MetadataHandler.extractTableMetadata(tableName);
//        Column[] columns = Extractor.extractColumns(tableMetadata);
//        String[] defaultValues = metadataHandler.extractDefaultValues(dataPairs.length, columns);
//
//        this.queryExecutor.insert(tableName, dataPairs, defaultValues);
//        System.out.println(SUCCESSFUL_ROW_INSERTION);
    }
}
