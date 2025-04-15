package org.watp.util.cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.watp.util.cache.annotaions.CacheEnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class CacheEnableAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CacheEnableAspect(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Pointcut("@annotation(org.watp.util.cache.annotaions.CacheEnable)")
    public void queryMethod() {
    }

    @Around("queryMethod()")
    public Object aroundQuery(ProceedingJoinPoint jp) {
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        if (method.getReturnType().getName().equals("void")) {
            logger.error("@CacheEnable should be signed on a method with return type");
            return null;
        }

        Map<String, Object> nameToArg = CacheKeyHelper.solveToNameToArgMap(jp);

        CacheEnable cacheEnable = method.getAnnotation(CacheEnable.class);
        String key = "";
        try {
            CacheKeyHelper.CacheKeyContext context = CacheKeyHelper.buildContext(cacheEnable);
            key = context.genKeyService.keyGen(context.metadata, nameToArg);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            logger.error("Exception in {}.{} with args: {}",
                    jp.getSignature().getDeclaringTypeName(),
                    jp.getSignature().getName(),
                    Arrays.toString(jp.getArgs()), e);
        }

        Object result = redisTemplate.opsForValue().get(key);
        if (Objects.isNull(result)) {
            synchronized(key.intern()) {
                result = redisTemplate.opsForValue().get(key);
                if (Objects.isNull(result)) {
                    try {
                        result = jp.proceed();
                    } catch (Throwable e) {
                        logger.error("Exception in {}.{} with args: {}",
                                jp.getSignature().getDeclaringTypeName(),
                                jp.getSignature().getName(),
                                Arrays.toString(jp.getArgs()), e);
                    }
                    if (!Objects.isNull(result)) {
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
