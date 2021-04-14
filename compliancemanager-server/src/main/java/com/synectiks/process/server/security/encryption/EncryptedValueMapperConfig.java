/*
 * */
package com.synectiks.process.server.security.encryption;

import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configures an {@link ObjectMapper} to enable database serialization for {@link EncryptedValue}.
 */
public class EncryptedValueMapperConfig {
    private static final String KEY = EncryptedValueMapperConfig.class.getCanonicalName();

    private enum Type {
        DATABASE
    }

    public static boolean isDatabase(DatabindContext ctx) {
        return Type.DATABASE.equals(ctx.getAttribute(KEY));
    }

    public static void enableDatabase(ObjectMapper objectMapper) {
        // The serializer and deserializer will switch modes depending on the attribute
        objectMapper
                .setConfig(objectMapper.getDeserializationConfig().withAttribute(KEY, Type.DATABASE))
                .setConfig(objectMapper.getSerializationConfig().withAttribute(KEY, Type.DATABASE));
    }
}
