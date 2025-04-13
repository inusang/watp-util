package org.watp.util.cache;

import java.util.Map;

public interface ICacheGenKeyService {
    String keyGen(CacheKeyMetadata metadata, Map<String,Object> nameToArg);
}
