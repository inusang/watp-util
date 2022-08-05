package org.watp.util.cache;

public abstract class AbstractStdCacheNaming {
    protected CacheType cacheType;
    protected String desc;
    protected String privateId;

    protected AbstractStdCacheNaming(CacheType cacheType, String desc, String privateId) {
        this.cacheType = cacheType;
        this.desc = desc;
        this.privateId = privateId;
    }
}
