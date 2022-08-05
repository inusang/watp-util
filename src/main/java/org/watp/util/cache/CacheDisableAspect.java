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

@Aspect
@Component
public class CacheDisableAspect implements IKeyGenerateAbility {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CacheDisableAspect(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Pointcut("@annotation(org.watp.util.cache.CacheDisable)")
    public void queryMethod() {

    }

    @Around("queryMethod()")
    public Object aroundQuery(ProceedingJoinPoint jp) {
        if (redisTemplate == null) {
            throw new NullPointerException("reactiveRedisTemplate is null in @CacheDisableAspect");
        }
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        if (!method.getReturnType().getName().equals("void")) {
            throw new IllegalArgumentException("@CacheDisable should be signed on a method without return type");
        }

        CacheDisable cacheDisable = ((MethodSignature) jp.getSignature()).getMethod().getAnnotation(CacheDisable.class);

        String cacheEnableElementTypes = cacheDisable.mappingElementTypes();
        cacheEnableElementTypes = cacheEnableElementTypes.replace("{", "").replace("}", "");
        String[] elementTypeStrArray = cacheEnableElementTypes.split(",");
        
        Class<?>[] elementTypeArray = new Class<?>[elementTypeStrArray.length];
        for (int i = 0; i < elementTypeStrArray.length; i++) {
            try {
                elementTypeArray[i] = Class.forName(elementTypeStrArray[i]);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        Method cacheEnableMethod;
        try {
            cacheEnableMethod = cacheDisable.mappingClass().getMethod(cacheDisable.mappingMethod(), elementTypeArray);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String key = keyGenerate(cacheEnableMethod, method.getParameters(), jp.getArgs(), cacheDisable.privateId());

        try {
            jp.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }

        redisTemplate.delete(key);

        return null;
    }

}
