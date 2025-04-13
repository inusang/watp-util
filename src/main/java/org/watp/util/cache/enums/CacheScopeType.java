package org.watp.util.cache.enums;

public enum CacheScopeType {
    SCOPED("Scoped"),
    GLOBAL("Global");

    private final String name;

    CacheScopeType(String type) {
        this.name = type;
    }

    public String getName() {
        return name;
    }

}
