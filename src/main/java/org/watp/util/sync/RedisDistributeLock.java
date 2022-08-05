package org.watp.util.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class RedisDistributeLock implements DistributeLock {
    private final Logger logger = LoggerFactory.getLogger(RedisDistributeLock.class);

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisDistributeLock(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void lock(String key, TimeUnit expirationUnit, long expiration, TimeUnit timeOutUnit, long timeOut) throws TimeoutException {
        lock(key, expirationUnit, expiration, timeOutUnit, timeOut, TimeUnit.MILLISECONDS, 50);
    }

    @Override
    public void lock(String key, TimeUnit expirationUnit, long expiration, TimeUnit timeOutUnit, long timeOut, TimeUnit sleepUnit, long sleep) throws TimeoutException {
        long tryLockTime = System.currentTimeMillis();
        while (!tryLock(key, expirationUnit, expiration)) {
            if (tryLockTime + timeOutUnit.toMillis(timeOut) < System.currentTimeMillis()) {
                throw new TimeoutException("Can not get distribute lock %s".formatted(key));
            }
            String currentExpirationStr = (String) redisTemplate.opsForValue().get(key);
            if (currentExpirationStr == null) {
                continue;
            }
            long currentExpiration = Long.parseLong(currentExpirationStr);
            if (System.currentTimeMillis() < currentExpiration) {
                try {
                    sleepUnit.sleep(sleep);
                } catch (InterruptedException e) {
                    synchronized (this) {
                        logger.warn("Distribute lock %s is interrupted".formatted(key), e);
                    }
                }
            }
        }
    }

    @Override
    public boolean tryLock(String key, TimeUnit expirationUnit, long expiration) {
        long lockTimeOut = -1;
        if (expiration != -1) {
            lockTimeOut = System.currentTimeMillis() + expirationUnit.toMillis(expiration);
        }
        if (Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(lockTimeOut)))) {
            return true;
        }
        String currentTimeOut = (String) redisTemplate.opsForValue().get(key);
        if ("-1".equals(currentTimeOut)) {
            return false;
        }
        if (StringUtils.hasLength(currentTimeOut) && Long.parseLong(currentTimeOut) < System.currentTimeMillis()) {
            String formerTimeOut = (String) redisTemplate.opsForValue().getAndSet(key, String.valueOf(lockTimeOut));
            return StringUtils.hasLength(formerTimeOut) && formerTimeOut.equals(currentTimeOut);
        }
        return false;
    }

    @Override
    public void unlock(String key) throws IllegalStateException {
        boolean res = Boolean.TRUE.equals(redisTemplate.delete(key));
        if (!res) {
            logger.error(("Distribute lock %s failed to release!").formatted(key));
            throw new IllegalStateException(("Distribute lock %s failed to release!").formatted(key));
        }
    }

}
