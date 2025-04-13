package org.watp.util.cache.annotaions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CacheDisable {
    Class<?> mappingClass();

    String mappingMethod();

    String mappingElementTypes();

    KeyAttributes[] keyAttributes();
}
