package org.watp.util.cache;

public interface IKeyGenerateAbility {
//    String PREFIX = "{";
//    String SUFFIX = "}";
//    String INTERVAL = ",";
//    String COLON = ":";
//
//    default String keyGenerate(Method method, Parameter[] parameters, Object[] args, String rawPrivateId) {
//        CacheEnable cacheEnable = method.getAnnotation(CacheEnable.class);
//        if (cacheEnable == null) {
//            throw new IllegalArgumentException("can not get @CacheEnable");
//        }
//        return keyGenerate(cacheEnable, parameters, args, rawPrivateId);
//    }
//
//    default String keyGenerate(CacheEnable cacheEnable, Parameter[] parameters, Object[] args, String rawPrivateExpress) {
//        Class<? extends ICacheGenKeyService> strategyClass = cacheEnable.keyGen();
//        if (strategyClass.getSuperclass() != CacheKeyMetadata.class) {
//            throw new IllegalArgumentException("the strategy class have to extends "+ CacheKeyMetadata.class);
//        }
//        CacheScopeType cacheScopeType = cacheEnable.type();
//        String desc = cacheEnable.desc();
//        CacheType dataType = cacheEnable.dataType();
//
//        StringBuilder privateId = new StringBuilder();
//        privateId.append(PREFIX);
//        if (rawPrivateExpress.matches(".*\\$\\{(.*)}.*")) {
//            String resolvingPrivateId = rawPrivateExpress.replace("${", "").replace("}", "");
//            for (String objExpress : resolvingPrivateId.split("\\|")) {
//                List<String> objNames = objExpress.contains(".") ? Lists.newArrayList(objExpress.split("\\.")) : Lists.newArrayList(objExpress);
//                Object privateIdObj = IntStream.range(0, parameters.length)
//                        //.filter(i -> parameters[i].getName().equals(objNames.get(0)))
//                        .mapToObj(i -> args[i])
//                        .collect(Collectors.toList()).get(0);
//                try {
//                    String strValue = resolvePrivateIdExp(objNames, privateIdObj);
//                    if (StringUtils.hasLength(strValue)) {
//                        privateId.append(objExpress).append(COLON)
//                                .append(strValue).append(INTERVAL);
//                    }
//                } catch (NoSuchFieldException | IllegalAccessException e) {
//                    e.printStackTrace();
//                    throw new IllegalArgumentException("the expression is not right");
//                }
//            }
//        }
//        privateId = privateId.deleteCharAt(privateId.lastIndexOf(INTERVAL));
//        privateId.append(SUFFIX);
//
//        ICacheGenKeyService strategy = null;
//        try {
//            Constructor<? extends ICacheGenKeyService> strategyConstructor = strategyClass.getDeclaredConstructor(CacheScopeType.class, String.class, CacheType.class, String.class);
//            strategyConstructor.setAccessible(true);
//            strategy = strategyConstructor.newInstance(cacheScopeType, desc, dataType, privateId.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (strategy == null) {
//            throw new IllegalStateException("can not get strategy");
//        }
//        String key = strategy.assemble();
//        if (key == null) {
//            throw new IllegalStateException("key generation failed");
//        }
//        return key;
//    }
//
//    private String resolvePrivateIdExp(List<String> objNames, Object obj) throws NoSuchFieldException, IllegalAccessException {
//        for (int i = 0; i < objNames.size(); i++) {
//            if ((i == objNames.size() - 1)) {
//                if (obj instanceof String) {
//                    return (String) obj;
//                } else if (!StringUtils.hasLength((String) obj)) {
//                    return null;
//                } else {
//                    throw new IllegalArgumentException("privateId must be a String");
//                }
//            }
//            Field field = obj.getClass().getDeclaredField(objNames.get(i + 1));
//            field.setAccessible(true);
//            obj = field.get(obj);
//        }
//        throw new IllegalArgumentException("can not find Parameter " + objNames.stream().collect(Collectors.joining(".", "(", ")")));
//    }

}
