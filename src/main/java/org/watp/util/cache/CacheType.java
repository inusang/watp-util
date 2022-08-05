package org.watp.util.cache;

public enum CacheType {
    PUBLIC("PUBLIC"),
    PRIVATE("PRIVATE");

    private final String name;

    CacheType(String type) {
        this.name = type;
    }

    public String getName() {
        return name;
    }

}
