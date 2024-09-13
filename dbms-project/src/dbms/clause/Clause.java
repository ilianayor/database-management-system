package dbms.clause;

public class Clause {
    private final String colName;
    private final String value;
    private final boolean shouldNegate;

    private Clause(String colName, String value, boolean shouldNegate) {
        this.colName = colName;
        this.value = value;
        this.shouldNegate = shouldNegate;
    }

    public static Clause build(String colName, String value, boolean shouldNegate) {
        return new Clause(colName, value, shouldNegate);
    }

    public String getValue() {
        return value;
    }

    public String getColName() {
        return colName;
    }

    public boolean getShouldNegate() {
        return shouldNegate;
    }

    @Override
    public String toString() {
        return colName + " " + value + " " + shouldNegate;
    }
}
