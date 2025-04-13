package org.watp.util.cache;

import com.google.common.collect.Lists;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.watp.util.cache.annotaions.CacheEnable;
import org.watp.util.cache.annotaions.KeyAttributes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Aspect
@Component
public class CacheEnableAspect {
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
            throw new IllegalArgumentException("@CacheKey should be signed on a method with return type");
        }

        List<String> parameterNames = Lists.newArrayList(((MethodSignature) jp.getSignature()).getParameterNames());
        List<Object> args = Lists.newArrayList(jp.getArgs());
        Map<String, Object> nameToArg = IntStream.range(0, parameterNames.size())
                .boxed()
                .collect(Collectors.toMap(
                        parameterNames::get,
                        args::get
                ));

        CacheEnable cacheEnable = method.getAnnotation(CacheEnable.class);
        String key = "";
        try {
            ICacheGenKeyService genKeyService;
            Constructor<? extends ICacheGenKeyService> serviceConstructor = cacheEnable.keyGen().getDeclaredConstructor();
            serviceConstructor.setAccessible(true);
            genKeyService = serviceConstructor.newInstance();
            CacheKeyMetadata metadata = CacheKeyMetadata.Builder.getBuilder()
                    .desc(cacheEnable.desc())
                    .cacheScope(cacheEnable.cacheScope())
                    .cacheType(cacheEnable.cacheType())
                    .keyAttributes(Arrays.stream(cacheEnable.keyAttributes())
                            .collect(Collectors.toMap(KeyAttributes::name, KeyAttributes::value))).build();
            key = genKeyService.keyGen(metadata, nameToArg);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }

        Object result = redisTemplate.opsForValue().get(key);
        if (Objects.isNull(result)) {
            synchronized(key.intern()) {
                result = redisTemplate.opsForValue().get(key);
                if (Objects.isNull(result)) {
                    try {
                        result = jp.proceed();
                    } catch (Throwable e) {
                        throw new RuntimeException("Business invocation failed", e);
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
