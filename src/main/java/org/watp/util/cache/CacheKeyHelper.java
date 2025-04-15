package org.watp.util.cache;

import com.google.common.collect.Lists;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.watp.util.cache.annotaions.CacheDisable;
import org.watp.util.cache.annotaions.CacheEnable;
import org.watp.util.cache.annotaions.KeyAttributes;
import org.watp.util.cache.enums.CacheScopeType;
import org.watp.util.cache.enums.CacheType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class CacheKeyHelper {
    static Map<String, Object> solveToNameToArgMap(ProceedingJoinPoint jp) {
        List<String> parameterNames = Lists.newArrayList(((MethodSignature) jp.getSignature()).getParameterNames());
        List<Object> args = Lists.newArrayList(jp.getArgs());
        return IntStream.range(0, parameterNames.size())
                .boxed()
                .collect(Collectors.toMap(
                        parameterNames::get,
                        args::get
                ));
    }

    static class CacheKeyContext {
        final ICacheGenKeyService genKeyService;
        final CacheKeyMetadata metadata;

        CacheKeyContext(ICacheGenKeyService genKeyService, CacheKeyMetadata metadata) {
            this.genKeyService = genKeyService;
            this.metadata = metadata;
        }

    }

    static CacheKeyContext buildContext(CacheEnable cacheEnable) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        return buildContext(cacheEnable.keyGen(), cacheEnable.desc(), cacheEnable.cacheScope(), cacheEnable.cacheType(), cacheEnable.keyAttributes());
    }

    static CacheKeyContext buildContext(CacheDisable cacheDisable) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        return buildContext(cacheDisable.keyGen(), cacheDisable.desc(), cacheDisable.cacheScope(), cacheDisable.cacheType(), cacheDisable.keyAttributes());
    }

    private static CacheKeyContext buildContext(Class<? extends ICacheGenKeyService> serviceClass,
                                                                 String desc, CacheScopeType cacheScope, CacheType cacheType,
                                                                 KeyAttributes[] keyAttributes) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        ICacheGenKeyService genKeyService;
        Constructor<? extends ICacheGenKeyService> serviceConstructor = serviceClass.getDeclaredConstructor();
        serviceConstructor.setAccessible(true);
        genKeyService = serviceConstructor.newInstance();
        CacheKeyMetadata metadata = CacheKeyMetadata.Builder.getBuilder()
                .desc(desc)
                .cacheScope(cacheScope)
                .cacheType(cacheType)
                .keyAttributes(Arrays.stream(keyAttributes)
                        .collect(Collectors.toMap(KeyAttributes::name, KeyAttributes::value)))
                .build();
        return new CacheKeyContext(genKeyService, metadata);
    }
}
