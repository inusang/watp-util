package org.watp.util.cache;

public enum DataType {
    LOCK("Lock"),
    DATA("Data");

    private final String name;

    DataType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
