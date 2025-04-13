package org.watp.util.cache.enums;

public enum CacheType {
    LOCK("Lock"),
    DATA("Data");

    private final String name;

    CacheType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
