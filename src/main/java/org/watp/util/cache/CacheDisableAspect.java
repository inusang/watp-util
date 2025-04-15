package org.watp.util.cache;

import com.google.common.collect.Lists;
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
import org.watp.util.cache.annotaions.CacheDisable;
import org.watp.util.cache.annotaions.CacheDisableGroup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class CacheDisableAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CacheDisableAspect(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Pointcut("@annotation(org.watp.util.cache.annotaions.CacheDisable) || @annotation(org.watp.util.cache.annotaions.CacheDisableGroup)")
    public void queryMethod() {
    }

    @Around("queryMethod()")
    public Object aroundQuery(ProceedingJoinPoint jp) {
        Object result;
        try {
            result = jp.proceed();
        } catch (Throwable e) {
            logger.error("Exception in {}.{} with args: {}",
                    jp.getSignature().getDeclaringTypeName(),
                    jp.getSignature().getName(),
                    Arrays.toString(jp.getArgs()), e);
            return null;
        }

        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        List<CacheDisable> disables = Lists.newArrayList();
        if (method.isAnnotationPresent(CacheDisable.class)) {
            disables.add(method.getAnnotation(CacheDisable.class));
        }
        if (method.isAnnotationPresent(CacheDisableGroup.class)) {
            CacheDisableGroup disableGroup = method.getAnnotation(CacheDisableGroup.class);
            disables.addAll(Lists.newArrayList(disableGroup.items()));
        }

        Map<String, Object> nameToArg = CacheKeyHelper.solveToNameToArgMap(jp);

        List<String> keys = Lists.newArrayList();
        for (CacheDisable cacheDisable : disables) {
            try {
                CacheKeyHelper.CacheKeyContext context = CacheKeyHelper.buildContext(cacheDisable);
                keys.add(context.genKeyService.keyGen(context.metadata, nameToArg));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                logger.error("Exception in {}.{} with args: {}",
                        jp.getSignature().getDeclaringTypeName(),
                        jp.getSignature().getName(),
                        Arrays.toString(jp.getArgs()), e);
            }
        }

        redisTemplate.delete(keys);

        return result;
    }

}
