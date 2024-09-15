package dbms.keyword;

public enum Keyword {
    WHERE("where"),
    DISTINCT("distinct"),
    INTO("into"),
    FROM("from"),
    STAR("*");

    private final String value;

    Keyword(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
