package dbms;

import dbms.strings.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static dbms.validator.table.TableValidator.DEFAULT_DATE_FORMAT;

public enum SupportedType {
    INTEGER("int"),
    STRING("string"),
    DATE("date"),
    UNKNOWN("unknown");

    private final String value;

    public static boolean isOfType(SupportedType supportedType, String toCheck) {
        try {
            if (supportedType == INTEGER) {
                return isInteger(toCheck);
            } else if (supportedType == STRING) {
                return isString(toCheck);
            } else if (supportedType == DATE) {
                return isDate(toCheck);
            }

            return false;

        } catch (Exception e) {
            return false;
        }
    }

    public static SupportedType getSupportedType(String toCheck) {
        if (isOfType(SupportedType.INTEGER, toCheck)) {
            return INTEGER;
        } else if (isOfType(SupportedType.STRING, toCheck)) {
            return STRING;
        } else if (isOfType(SupportedType.DATE, toCheck)) {
            return DATE;
        }

        return UNKNOWN;
    }

    public static boolean isInteger(String toCheck) {
        try {
            Integer.parseInt(toCheck);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isString(String toCheck) {
        return StringUtils.startsWith(toCheck, "\"") && StringUtils.endsWith(toCheck, "\"");
    }

    public static boolean isDate(String toCheck) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
            LocalDate.parse(toCheck, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static SupportedType toSupportedType(String str) {
        for (SupportedType supportedType : SupportedType.values()) {
            if (str.equals(supportedType.toString())) {
                return supportedType;
            }
        }

        return UNKNOWN;
    }

    SupportedType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
