package dbms.clause;

import java.util.Objects;

public class Clause {
    private final TypeValuePair typeValuePair;
    private final Sign sign;

    private Clause(TypeValuePair typeValuePair, Sign sign) {
        this.typeValuePair = typeValuePair;
        this.sign = sign;
    }

    public static Clause build(TypeValuePair typeValuePair, Sign sign) {
        return new Clause(typeValuePair, sign);
    }

    public static Clause empty() {
        return new Clause(null, Sign.UNKNOWN);
    }

    public TypeValuePair getTypeValuePair() {
        return typeValuePair;
    }

    public Sign getSign() {
        return sign;
    }

    @Override
    public String toString() {
        return typeValuePair.getColName() + " " + typeValuePair.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clause clause = (Clause) o;
        return Objects.equals(typeValuePair, clause.typeValuePair) && sign == clause.sign;
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeValuePair, sign);
    }
}
