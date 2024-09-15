package dbms.command;

public enum Command {
    CREATE_TABLE("CreateTable"),
    DROP_TABLE("DropTable"),
    LIST_TABLES("ListTables"),
    TABLE_INFO("TableInfo"),
    INSERT("Insert"),
    DELETE("Delete"),
    SELECT("Select"),
    QUIT("quit");

    private final String value;

    Command(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
