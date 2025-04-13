package org.watp.util.cache;

import com.google.common.base.Objects;
import org.watp.util.cache.enums.CacheScopeType;
import org.watp.util.cache.enums.CacheType;

import java.util.Map;

public class CacheKeyMetadata {
    private String desc;
    private CacheType cacheType;
    private CacheScopeType cacheScope;
    private Map<String,String> keyAttributes;

    public CacheKeyMetadata(String desc, CacheType cacheType, CacheScopeType cacheScope, Map<String, String> keyAttributes) {
        this.desc = desc;
        this.cacheType = cacheType;
        this.cacheScope = cacheScope;
        this.keyAttributes = keyAttributes;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public CacheType getCacheType() {
        return cacheType;
    }

    public void setCacheType(CacheType cacheType) {
        this.cacheType = cacheType;
    }

    public CacheScopeType getCacheScope() {
        return cacheScope;
    }

    public void setCacheScope(CacheScopeType cacheScope) {
        this.cacheScope = cacheScope;
    }

    public Map<String, String> getKeyAttributes() {
        return keyAttributes;
    }

    public void setKeyAttributes(Map<String, String> keyAttributes) {
        this.keyAttributes = keyAttributes;
    }

    public static final class Builder {
        private String desc;
        private CacheType cacheType;
        private CacheScopeType cacheScope;
        private Map<String,String> keyAttributes;

        private Builder() {
        }

        public static Builder getBuilder() {
            return new Builder();
        }

        public Builder desc(String desc) {
            this.desc = desc;
            return this;
        }

        public Builder cacheType(CacheType cacheType) {
            this.cacheType = cacheType;
            return this;
        }

        public Builder cacheScope(CacheScopeType cacheScope) {
            this.cacheScope = cacheScope;
            return this;
        }

        public Builder keyAttributes(Map<String, String> keyAttributes) {
            this.keyAttributes = keyAttributes;
            return this;
        }

        public CacheKeyMetadata build() {
            return new CacheKeyMetadata(desc, cacheType, cacheScope, keyAttributes);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CacheKeyMetadata that = (CacheKeyMetadata) o;
        return Objects.equal(desc, that.desc) && cacheType == that.cacheType && cacheScope == that.cacheScope && Objects.equal(keyAttributes, that.keyAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(desc, cacheType, cacheScope, keyAttributes);
    }

    @Override
    public String toString() {
        return "CacheKeyMetadata{" +
                "desc='" + desc + '\'' +
                ", cacheType=" + cacheType +
                ", cacheScope=" + cacheScope +
                ", keyAttributes=" + keyAttributes +
                '}';
    }
}
