package org.watp.util.sync;

import org.watp.util.cache.CacheEnable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DLock {
    Class<? extends DistributeLock> lock() default RedisDistributeLock.class;

    CacheEnable cacheEnable();

    LockStrategy lockStrategy();

    /**
     * the parameter below is only used in LockStrategy.LOCK mode
     */
    TimeUnit timeOutUnit() default TimeUnit.SECONDS;

    long timeOut() default 5;

    TimeUnit sleepUnit() default TimeUnit.MILLISECONDS;

    long sleep() default 50;
}
