package dbms.keyword;

public enum Keyword {
    WHERE("where"),
    DISTINCT("distinct"),
    INTO("into"),
    FROM("from"),
    STAR("*"),
    NOT("not");

    private final String value;

    Keyword(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
