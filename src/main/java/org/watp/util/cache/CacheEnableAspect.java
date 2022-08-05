package org.watp.util.cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class CacheEnableAspect implements IKeyGenerateAbility {
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CacheEnableAspect(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Pointcut("@annotation(org.watp.util.cache.CacheEnable)")
    public void queryMethod() {

    }

    @Around("queryMethod()")
    public Object aroundQuery(ProceedingJoinPoint jp) {
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        if (method.getReturnType().getName().equals("void")) {
            throw new IllegalArgumentException("@CacheKey should be signed on a method with return type");
        }

        CacheEnable cacheEnable = ((MethodSignature) jp.getSignature()).getMethod().getAnnotation(CacheEnable.class);
        String key = keyGenerate(method, method.getParameters(), jp.getArgs(), cacheEnable.privateId());

        Object result = redisTemplate.opsForValue().get(key);
        if (result == null) {
            synchronized(key.intern()) {
                result = redisTemplate.opsForValue().get(key);
                if (result == null) {
                    try {
                        result = jp.proceed();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    if (result != null) {
                        long expiration = cacheEnable.expiration();
                        TimeUnit expirationUnit = cacheEnable.expirationUnit();
                        if (expiration < 0) {
                            redisTemplate.opsForValue().set(key, result);
                        } else {
                            redisTemplate.opsForValue().set(key, result, Duration.of(expiration, expirationUnit.toChronoUnit()));
                        }
                    }
                }
            }
        }

        return result;
    }

}
