/*
 * */
package com.synectiks.process.server.bindings.providers;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.synectiks.process.server.indexer.retention.strategies.UnknownRetentionStrategyConfig;
import com.synectiks.process.server.jackson.MongoJodaDateTimeDeserializer;
import com.synectiks.process.server.jackson.MongoJodaDateTimeSerializer;
import com.synectiks.process.server.jackson.MongoZonedDateTimeDeserializer;
import com.synectiks.process.server.jackson.MongoZonedDateTimeSerializer;
import com.synectiks.process.server.plugin.indexer.retention.RetentionStrategyConfig;
import com.synectiks.process.server.security.encryption.EncryptedValueMapperConfig;

import org.joda.time.DateTime;
import org.mongojack.internal.MongoJackModule;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.IOException;
import java.time.ZonedDateTime;

@Singleton
public class MongoJackObjectMapperProvider implements Provider<ObjectMapper> {
    private final ObjectMapper objectMapper;

    @Inject
    public MongoJackObjectMapperProvider(ObjectMapper objectMapper) {
        // add the mongojack specific stuff on a copy of the original ObjectMapper to avoid changing the singleton instance
        this.objectMapper = objectMapper.copy()
                .addHandler(new ReplaceUnknownSubtypesWithFallbackHandler())
                .setPropertyNamingStrategy(new PreserveLeadingUnderscoreStrategy())
                .registerModule(new SimpleModule("JSR-310-MongoJack")
                        .addSerializer(ZonedDateTime.class, new MongoZonedDateTimeSerializer())
                        .addDeserializer(ZonedDateTime.class, new MongoZonedDateTimeDeserializer())
                        .addSerializer(DateTime.class, new MongoJodaDateTimeSerializer())
                        .addDeserializer(DateTime.class, new MongoJodaDateTimeDeserializer()));

        MongoJackModule.configure(this.objectMapper);
        EncryptedValueMapperConfig.enableDatabase(this.objectMapper);
    }

    @Override
    public ObjectMapper get() {
        return objectMapper;
    }

    /**
     * This abomination is necessary because when using MongoJack to read "_id" object ids back from the database
     * the property name isn't correctly mapped to the POJO using the LowerCaseWithUnderscoresStrategy.
     * <p>
     * Apparently no one in the world tried to use a different naming strategy with MongoJack.
     * (one of my many useless talents is finding corner cases).
     * </p>
     */
    public static class PreserveLeadingUnderscoreStrategy extends PropertyNamingStrategy.SnakeCaseStrategy {
        @Override
        public String translate(String input) {
            String translated = super.translate(input);
            if (input.startsWith("_") && !translated.startsWith("_")) {
                translated = "_" + translated; // lol underscore
            }
            return translated;
        }
    }

    // TODO this should be pluggable to allow subsystems to specify their own fallback types instead of hardcoding it there.
    private static class ReplaceUnknownSubtypesWithFallbackHandler extends DeserializationProblemHandler {
        @Override
        public JavaType handleUnknownTypeId(DeserializationContext ctxt, JavaType baseType, String subTypeId, TypeIdResolver idResolver, String failureMsg) throws IOException {
            if (baseType.getRawClass().equals(RetentionStrategyConfig.class)) {
                return SimpleType.constructUnsafe(UnknownRetentionStrategyConfig.class);
            }
            return super.handleUnknownTypeId(ctxt, baseType, subTypeId, idResolver, failureMsg);
        }
    }
}
