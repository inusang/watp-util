package org.watp.util.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CacheEnable {
    Class<? extends ICacheKeyNamingStrategy> keyGen() default StdKeyNamingStrategy.class;

    CacheType type() default CacheType.PUBLIC;

    String desc();

    DataType dataType();

    String privateId() default "";

    long expiration() default -1;

    TimeUnit expirationUnit() default TimeUnit.HOURS;
}
