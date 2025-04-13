package org.watp.util.sync;

import org.watp.util.ResponseVO;
import org.watp.util.cache.annotaions.CacheEnable;

import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public enum LockStrategy {
    LOCK {
        @Override
        public String syncFlow(DistributeLock lock, String key, DLock dLock, Supplier<String> businessSupplier) {
            String result;
            CacheEnable cacheEnable = dLock.cacheEnable();
            try {
                lock.lock(key, cacheEnable.expirationUnit(), cacheEnable.expiration(), dLock.timeOutUnit(), dLock.timeOut(), dLock.sleepUnit(), dLock.sleep());
                result = businessSupplier.get();
                try {
                    lock.unlock(key);
                } catch (IllegalStateException ise) {
                    ise.printStackTrace();
                }
            } catch (TimeoutException e) {
                result = ResponseVO.GeneralResponse.DEALING_BUSINESS.getResponse().toJson();
            } catch (Throwable e) {
                try {
                    lock.unlock(key);
                } catch (IllegalStateException ise) {
                    ise.printStackTrace();
                }
                result = ResponseVO.GeneralResponse.SYS_ERROR.getResponse().toJson();
                e.printStackTrace();
            }
            return result;
        }
    }, TRYLOCK {
        @Override
        public String syncFlow(DistributeLock lock, String key, DLock dLock, Supplier<String> businessSupplier) {
            String result;
            CacheEnable cacheEnable = dLock.cacheEnable();
            if (lock.tryLock(key, cacheEnable.expirationUnit(), cacheEnable.expiration())) {
                try {
                    result = businessSupplier.get();
                } catch (Throwable e) {
                    result = ResponseVO.GeneralResponse.SYS_ERROR.getResponse().toJson();
                    e.printStackTrace();
                } finally {
                    try {
                        lock.unlock(key);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                result = ResponseVO.GeneralResponse.DEALING_BUSINESS.getResponse().toJson();
            }
            return result;
        }
    };

    public abstract String syncFlow(DistributeLock lock, String key, DLock dLock, Supplier<String> businessSupplier);
}
