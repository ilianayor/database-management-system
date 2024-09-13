package dbms.executor.table;

import dbms.SupportedType;

public class Column {
    private final SupportedType supportedType;
    private final String name;
    private final String defaultValue;

    public Column(SupportedType supportedType, String name, String defaultValue) {
        this.supportedType = supportedType;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public Column(SupportedType supportedType, String name) {
        this.supportedType = supportedType;
        this.name = name;
        this.defaultValue = "";
    }

    public String getName() {
        return name;
    }

    public SupportedType getSupportedType() {
        return supportedType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
