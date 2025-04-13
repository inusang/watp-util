package org.watp.util.sync;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.watp.util.ResponseVO;
import org.watp.util.cache.annotaions.CacheEnable;
import org.watp.util.cache.IKeyGenerateAbility;

import java.lang.reflect.Method;

@Aspect
@Component
public class DLockAspect implements IKeyGenerateAbility {
    private final DistributeLock lock;

    @Autowired
    private DLockAspect(DistributeLock lock) {
        this.lock = lock;
    }

    @Pointcut("@annotation(org.watp.util.sync.DLock)")
    public void dLockMethod() {
    }

    @Around("dLockMethod()")
    public String aroundDLockMethod(ProceedingJoinPoint jp) {
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        DLock dLock = ((MethodSignature) jp.getSignature()).getMethod().getAnnotation(DLock.class);
        CacheEnable cacheEnable = dLock.cacheEnable();
//        String key = keyGenerate(cacheEnable, method.getParameters(), jp.getArgs(), cacheEnable.privateId());
//        LockStrategy lockStrategy = dLock.lockStrategy();
        String result = null;
//        result = lockStrategy.syncFlow(lock, key, dLock, () -> {
//            try {
//                return (String) jp.proceed();
//            } catch (Throwable e) {
//                e.printStackTrace();
//                return ResponseVO.GeneralResponse.SYS_ERROR.getResponse().toJson();
//            }
//        });
        return result;
    }
}
