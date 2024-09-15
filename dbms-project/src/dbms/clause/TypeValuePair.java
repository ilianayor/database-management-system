package dbms.clause;

import dbms.SupportedType;

import java.util.Objects;

public class TypeValuePair {
    private final String colName;
    private final String value;
    private final SupportedType supportedType;

    private TypeValuePair(String colName, String value, SupportedType supportedType) {
        this.colName = colName;
        this.value = value;
        this.supportedType = supportedType;
    }

    public static TypeValuePair of(String colName, String value, SupportedType supportedType) {
        return new TypeValuePair(colName, value, supportedType);
    }

    public String getColName() {
        return colName;
    }

    public String getValue() {
        return value;
    }

    public SupportedType getSupportedType() {
        return supportedType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeValuePair that = (TypeValuePair) o;
        return Objects.equals(colName, that.colName) && Objects.equals(value, that.value);
    }

    @Override
    public String toString() {
        return "TypeValuePair{" +
                "colName='" + colName + '\'' +
                ", value='" + value + '\'' +
                ", supportedType=" + supportedType +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(colName, value);
    }
}
