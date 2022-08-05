package org.watp.util.sync;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface DistributeLock {
    /**
     * expiration means how long this business will be locked,
     * timeOut means how long this business will wait for get lock
     * sleep means the interval to try to get the lock
     */
    void lock(String key, TimeUnit expirationUnit, long expiration, TimeUnit timeOutUnit, long timeOut, TimeUnit sleepUnit, long sleep) throws TimeoutException;

    void lock(String key, TimeUnit expirationUnit, long expiration, TimeUnit timeOutUnit, long timeOut) throws TimeoutException;

    boolean tryLock(String key, TimeUnit expirationUnit, long expiration);

    void unlock(String key) throws IllegalStateException;
}
