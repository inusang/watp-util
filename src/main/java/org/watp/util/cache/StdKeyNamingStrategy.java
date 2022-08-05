package org.watp.util.cache;

import com.google.common.collect.Lists;

import java.util.List;

public class StdKeyNamingStrategy extends AbstractStdCacheNaming implements ICacheKeyNamingStrategy {

    private final DataType dataType;

    private StdKeyNamingStrategy(CacheType cacheType, String desc, DataType dataType, String privateId) {
        super(cacheType, desc, privateId);
        this.dataType = dataType;
    }

    public static final class Builder {
        private CacheType cacheType;
        private String desc;
        private DataType dataType;
        private String privateId;

        private Builder() {
        }

        public static Builder getBuilder() {
            return new Builder();
        }

        public Builder cacheType(CacheType cacheType) {
            this.cacheType = cacheType;
            return this;
        }

        public Builder desc(String desc) {
            this.desc = desc;
            return this;
        }

        public Builder dataType(DataType dataType) {
            this.dataType = dataType;
            return this;
        }

        public Builder privateId(String privateId) {
            this.privateId = privateId;
            return this;
        }

        public StdKeyNamingStrategy build() {
            return new StdKeyNamingStrategy(cacheType, desc, dataType, privateId);
        }
    }

    @Override
    public String assembleTemplate() {
        if (cacheType == CacheType.PUBLIC) {
            return DEFAULT_PUBLIC_TEMPLATE.replace("[=]", "DATATYPE-${}");
        } else if (cacheType == CacheType.PRIVATE) {
            return DEFAULT_PRIVATE_TEMPLATE.replace("[=]", "DATATYPE-${}");
        } else {
            throw new IllegalArgumentException("can not resolve cache type");
        }
    }

    @Override
    public List<String> assembleParamParts() {
        if (cacheType == CacheType.PUBLIC) {
            if (desc == null || dataType == null) {
                throw new IllegalArgumentException("parameter desc, entityName can not be null");
            }
            List<String> privateParts = Lists.newArrayList(desc, dataType.getName());
            return privateParts;
        } else {
            if (desc == null || dataType == null || privateId == null) {
                throw new IllegalArgumentException("parameter desc, entityName, privateId can not be null");
            }
            List<String> privateParts = Lists.newArrayList(desc, dataType.getName(), privateId);
            return privateParts;
        }
    }

}
