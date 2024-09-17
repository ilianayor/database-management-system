package dbms.executor.query;

import dbms.SupportedType;

public class DataPair {
    private final String value;
    private final SupportedType supportedType;

    public static DataPair[] calculate(String[] args) {
        DataPair[] result = new DataPair[args.length];
        int index = 0;

        for (String arg : args) {
            if (SupportedType.isInteger(arg)) {
                DataPair pair = new DataPair(arg, SupportedType.INTEGER);
                result[index++] = pair;
            }  else if (SupportedType.isString(arg)) {
                DataPair pair = new DataPair(arg, SupportedType.STRING);
                result[index++] = pair;
            } else if (SupportedType.isDate(arg)) {
                DataPair pair = new DataPair(arg, SupportedType.DATE);
                result[index++] = pair;
            }
        }

        return result;
    }

    public DataPair(String value, SupportedType supportedType) {
        this.value = value;
        this.supportedType = supportedType;
    }

    public SupportedType getSupportedType() {
        return supportedType;
    }

    public String getValue() {
        return value;
    }
}
