package org.watp.util.cache;

import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultCacheGenKeyService implements ICacheGenKeyService {
    @Override
    public String keyGen(CacheKeyMetadata metadata, Map<String,Object> nameToArg) {
        StringBuilder key = new StringBuilder();
        key.append(metadata.getCacheType().getName())
                .append(":").append(metadata.getCacheScope().getName())
                .append(":").append(metadata.getDesc());

        Map<String,String> keyAttributes = metadata.getKeyAttributes();
        for (Map.Entry<String,String> entry : keyAttributes.entrySet()) {
            if (Objects.isNull(entry.getKey()) || entry.getKey().isEmpty()) {
                continue;
            }
            key.append(":");
            String exp = entry.getValue();
            if (!exp.matches(".*\\$\\{(.*)}.*")) {
                key.append(entry.getKey()).append("=").append(exp);
                continue;
            }

            Pattern pattern = Pattern.compile("\\$\\{(.*)}");
            Matcher matcher = pattern.matcher(exp);
            int formerIndex = 0;
            while (matcher.find()) {
                key.append(entry.getKey()).append("=").append(exp, formerIndex, matcher.start());
                String objName = exp.substring(matcher.start() + 2, matcher.end() - 1);

                List<String> fieldPathSegments = Lists.newArrayList(objName.split("\\."));
                if (fieldPathSegments.isEmpty()) {
                    throw new RuntimeException("Invalid expression: field path is empty or malformed in placeholder '" + exp + "'");
                }
                Object current = nameToArg.get(fieldPathSegments.getFirst());

                try {
                    for (int i = 1; i < fieldPathSegments.size(); i++) {
                        if (current == null) break;

                        String fieldName = fieldPathSegments.get(i);
                        Field field = getFieldRecursively(current.getClass(), fieldName);
                        field.setAccessible(true);
                        current = field.get(current);
                    }

                    if (current != null) {
                        key.append(current);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to resolve field path: " + String.join(".", fieldPathSegments), e);
                }

                formerIndex = matcher.end() + 1;
            }
            if (formerIndex < exp.length()) {
                key.append(exp, formerIndex - 1, exp.length());
            }

        }

        return key.toString();
    }

    private Field getFieldRecursively(Class<?> clazz, String name) throws NoSuchFieldException {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field '" + name + "' not found in class hierarchy.");
    }
}
