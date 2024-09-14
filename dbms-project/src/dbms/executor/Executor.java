package dbms.executor;

import dbms.Printer;
import dbms.SuccessMessages;
import dbms.SupportedType;
import dbms.clause.Clause;
import dbms.clause.Sign;
import dbms.clause.TypeValuePair;
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

import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static dbms.SuccessMessages.SUCCESSFUL_ROW_INSERTION;
import static dbms.validator.table.TableValidator.DEFAULT_DATE_FORMAT;


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
        String tableName = p[1];

        if (StringUtils.contains(pair.getArgs(), "where")) {
            Clause clause = Extractor.extractClause(p[p.length - 1]);
            String[] lines = FileSystemExecutor.loadInMemory(tableName);
            String tableMetadata = MetadataHandler.extractTableMetadata(tableName);
            int indexOfOpeningBracket = StringUtils.indexOf(tableMetadata, '(');
            int indexOfClosingBracket = StringUtils.indexOf(tableMetadata, ')');
            Column[] columns = Extractor.extractColumns(StringUtils.substring(tableMetadata, indexOfOpeningBracket + 1, indexOfClosingBracket));
            TypeValuePair[][] pairs = match(lines, columns);
            String[] toWrite = apply(clause, pairs);

            if (toWrite.length == 0) {
                FileSystemExecutor.clear(tableName);
            } else {
                FileSystemExecutor.override(tableName, toWrite);
            }

            System.out.println("Rows affected: " + (pairs.length - toWrite.length));
            return;
        }

        int numberOfLines = FileSystemExecutor.countNumberOfLines(tableName);
        FileSystemExecutor.clear(tableName);
        System.out.println("Rows affected: " + numberOfLines);
    }

    private String[] apply(Clause clause, TypeValuePair[][] typeValuePairs) {
        int numberOfValidRows = countNumberOfValidRows(clause, typeValuePairs);
        String[] validRows = new String[typeValuePairs.length - numberOfValidRows];
        int index = 0;

        for (TypeValuePair[] typeValuePair : typeValuePairs) {
            for (int j = 0; j < typeValuePair.length; j++) {
                if (typeValuePair[j].getColName().equals(clause.getTypeValuePair().getColName())) {
                    if (!isClauseApplicable(typeValuePair[j], clause)) {
                        validRows[index++] = format(typeValuePair);
                    }

                    break;
                }
            }
        }

        return validRows;
    }

    private String format(TypeValuePair[] typeValuePairs) {
        String[] intermediate = new String[typeValuePairs.length];
        int index = 0;

        for (TypeValuePair typeValuePair : typeValuePairs) {
            intermediate[index++] = typeValuePair.getValue();
        }

        return StringUtils.join(intermediate, ",");
    }

    private int countNumberOfValidRows(Clause clause, TypeValuePair[][] typeValuePairs) {
        int cnt = 0;

        for (TypeValuePair[] typeValuePair : typeValuePairs) {
            for (int j = 0; j < typeValuePair.length; j++) {
                if (typeValuePair[j].getColName().equals(clause.getTypeValuePair().getColName())) {
                    if (isClauseApplicable(typeValuePair[j], clause)) {
                        cnt++;
                    }

                    break;
                }
            }
        }

        return cnt;
    }

    private boolean isClauseApplicable(TypeValuePair typeValuePair, Clause clause) {
        Sign sign = clause.getSign();

        if (typeValuePair.getSupportedType() == SupportedType.INTEGER) {
            int actualColValue = Integer.parseInt(typeValuePair.getValue());
            int valueToCompare = Integer.parseInt(clause.getTypeValuePair().getValue());

            return isClauseApplicableInteger(actualColValue, sign, valueToCompare);
        } else if (typeValuePair.getSupportedType() == SupportedType.STRING) {
            String actualColValue = typeValuePair.getValue();
            String valueToCompare = clause.getTypeValuePair().getValue();

            return isClauseApplicableString(actualColValue, sign, valueToCompare);
        } else if (typeValuePair.getSupportedType() == SupportedType.DATE) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);

            LocalDate actualColValue = LocalDate.parse(typeValuePair.getValue(), formatter);
            LocalDate valueToCompare = LocalDate.parse(clause.getTypeValuePair().getValue(), formatter);

            return isClauseApplicableLocalDate(actualColValue, sign, valueToCompare);
        }

        return false;
    }

    private boolean isClauseApplicableLocalDate(LocalDate actualColValue, Sign sign, LocalDate valueToCompare) {
        if (sign == Sign.EQUALS) {
            return actualColValue.equals(valueToCompare);
        } else if (sign == Sign.NOT_EQUALS) {
            return !actualColValue.equals(valueToCompare);
        } else if (sign == Sign.LESS_THAN) {
            return actualColValue.isBefore(valueToCompare);
        } else if (sign == Sign.LESS_THAN_OR_EQUALS) {
            return actualColValue.isBefore(valueToCompare) || actualColValue.equals(valueToCompare);
        } else if (sign == Sign.GREATER_THAN) {
            return actualColValue.isAfter(valueToCompare);
        } else if (sign == Sign.GREATER_THAN_OR_EQUALS) {
            return actualColValue.isAfter(valueToCompare) || actualColValue.equals(valueToCompare);
        }

        return false;
    }

    private boolean isClauseApplicableString(String actualColValue, Sign sign, String valueToCompare) {
        if (sign == Sign.EQUALS) {
            return actualColValue.equals(valueToCompare);
        } else if (sign == Sign.NOT_EQUALS) {
            return !actualColValue.equals(valueToCompare);
        } else if (sign == Sign.LESS_THAN) {
            return StringUtils.isLessThan(actualColValue, valueToCompare);
        } else if (sign == Sign.LESS_THAN_OR_EQUALS) {
            return StringUtils.isLessThan(actualColValue, valueToCompare) || actualColValue.equals(valueToCompare);
        } else if (sign == Sign.GREATER_THAN) {
            return !(StringUtils.isLessThan(actualColValue, valueToCompare) || actualColValue.equals(valueToCompare));
        } else if (sign == Sign.GREATER_THAN_OR_EQUALS) {
            return !(StringUtils.isLessThan(actualColValue, valueToCompare));
        }

        return false;
    }

    private boolean isClauseApplicableInteger(int actualColValue, Sign sign, int valueToCompare) {
        if (sign == Sign.EQUALS) {
            return actualColValue == valueToCompare;
        } else if (sign == Sign.NOT_EQUALS) {
            return actualColValue != valueToCompare;
        } else if (sign == Sign.LESS_THAN) {
            return actualColValue < valueToCompare;
        } else if (sign == Sign.LESS_THAN_OR_EQUALS) {
            return actualColValue <= valueToCompare;
        } else if (sign == Sign.GREATER_THAN) {
            return actualColValue > valueToCompare;
        } else if (sign == Sign.GREATER_THAN_OR_EQUALS) {
            return actualColValue >= valueToCompare;
        }

        return false;
    }

    private TypeValuePair[][] match(String[] lines, Column[] columns) {
        TypeValuePair[][] typeValuePairs = new TypeValuePair[lines.length][columns.length];

        for (int i = 0; i < lines.length; i++) {
            String[] splittedLines = StringUtils.split(lines[i], ',');

            for (int j = 0; j < columns.length; j++) {
                typeValuePairs[i][j] = TypeValuePair.of(columns[j].getName(), splittedLines[j], columns[j].getSupportedType());
            }
        }

        return typeValuePairs;
    }
}
