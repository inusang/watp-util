package org.watp.util.cache.annotaions;

import org.watp.util.cache.*;
import org.watp.util.cache.enums.CacheScopeType;
import org.watp.util.cache.enums.CacheType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CacheEnable {
    Class<? extends ICacheGenKeyService> keyGen() default DefaultCacheGenKeyService.class;

    String desc();

    CacheType cacheType() default CacheType.DATA;

    CacheScopeType cacheScope();

    KeyAttributes[] keyAttributes();

    long expiration() default -1;

    TimeUnit expirationUnit() default TimeUnit.HOURS;
}
