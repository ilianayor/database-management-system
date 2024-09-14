package dbms.clause;

public enum Sign {
    EQUALS("="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUALS("<="),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUALS(">="),
    NOT_EQUALS("!="),
    UNKNOWN("unknown");

    private final String value;

    Sign(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Sign toSign(String str) {
        for (Sign s : Sign.values()) {
            if (str.equals(s.getValue())) {
                return s;
            }
        }

        return Sign.UNKNOWN;
    }
}
