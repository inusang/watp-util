package org.watp.util.cache;

import java.util.List;

public interface ICacheKeyNamingStrategy {

    String DEFAULT_PUBLIC_TEMPLATE = "PUBLIC-DESC-${}-[=]";
    String DEFAULT_PRIVATE_TEMPLATE = "PRIVATE-DESC-${}-[=]-ID-${}";

    default String assemble() {
        String template = this.assembleTemplate();
        List<String> parts = this.assembleParamParts();
        if ((template.length() - template.replace("${}", "").length())/3 != parts.size()) {
            throw new IllegalStateException("The quantity of the placeHolders ${} does not match private parts");
        }
        int placeHolderIndex = 0;
        while (template.contains("${}")) {
            template = template.replaceFirst("\\$\\{}", parts.get(placeHolderIndex++));
        }
        return template;
    }

    String assembleTemplate();

    List<String> assembleParamParts();

}
