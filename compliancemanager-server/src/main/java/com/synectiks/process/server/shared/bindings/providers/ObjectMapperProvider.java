/*
 * */
package com.synectiks.process.server.shared.bindings.providers;

import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.zafarkhaja.semver.Version;
import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.grn.GRNDeserializer;
import com.synectiks.process.common.grn.GRNKeyDeserializer;
import com.synectiks.process.common.grn.GRNRegistry;
import com.synectiks.process.server.database.ObjectIdSerializer;
import com.synectiks.process.server.jackson.AutoValueSubtypeResolver;
import com.synectiks.process.server.jackson.JodaTimePeriodKeyDeserializer;
import com.synectiks.process.server.jackson.SemverDeserializer;
import com.synectiks.process.server.jackson.SemverRequirementDeserializer;
import com.synectiks.process.server.jackson.SemverRequirementSerializer;
import com.synectiks.process.server.jackson.SemverSerializer;
import com.synectiks.process.server.jackson.VersionDeserializer;
import com.synectiks.process.server.jackson.VersionSerializer;
import com.synectiks.process.server.plugin.inject.JacksonSubTypes;
import com.synectiks.process.server.security.encryption.EncryptedValue;
import com.synectiks.process.server.security.encryption.EncryptedValueDeserializer;
import com.synectiks.process.server.security.encryption.EncryptedValueSerializer;
import com.synectiks.process.server.security.encryption.EncryptedValueService;
import com.synectiks.process.server.shared.jackson.SizeSerializer;
import com.synectiks.process.server.shared.plugins.GraylogClassLoader;
import com.synectiks.process.server.shared.rest.RangeJsonSerializer;
import com.synectiks.process.server.semver4j.Requirement;
import com.synectiks.process.server.semver4j.Semver;

import org.joda.time.Period;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Singleton
public class ObjectMapperProvider implements Provider<ObjectMapper> {
    protected final ObjectMapper objectMapper;

    // WARNING: This constructor should ONLY be used for tests!
    public ObjectMapperProvider() {
        this(ObjectMapperProvider.class.getClassLoader(), Collections.emptySet(), new EncryptedValueService(UUID.randomUUID().toString()), GRNRegistry.createWithBuiltinTypes());
    }

    // WARNING: This constructor should ONLY be used for tests!
    public ObjectMapperProvider(ClassLoader classLoader, Set<NamedType> subtypes) {
        this(classLoader, subtypes, new EncryptedValueService(UUID.randomUUID().toString()), GRNRegistry.createWithBuiltinTypes());
    }

    @Inject
    public ObjectMapperProvider(@GraylogClassLoader final ClassLoader classLoader,
                                @JacksonSubTypes Set<NamedType> subtypes,
                                EncryptedValueService encryptedValueService,
                                GRNRegistry grnRegistry) {
        final ObjectMapper mapper = new ObjectMapper();
        final TypeFactory typeFactory = mapper.getTypeFactory().withClassLoader(classLoader);
        final AutoValueSubtypeResolver subtypeResolver = new AutoValueSubtypeResolver();

        this.objectMapper = mapper
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .disable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY)
                .setPropertyNamingStrategy(new PropertyNamingStrategy.SnakeCaseStrategy())
                .setSubtypeResolver(subtypeResolver)
                .setTypeFactory(typeFactory)
                .registerModule(new GuavaModule())
                .registerModule(new JodaModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.SECONDS, false))
                .registerModule(new SimpleModule("alertmanager")
                        .addKeyDeserializer(Period.class, new JodaTimePeriodKeyDeserializer())
                        .addKeyDeserializer(GRN.class, new GRNKeyDeserializer(grnRegistry))
                        .addSerializer(new RangeJsonSerializer())
                        .addSerializer(new SizeSerializer())
                        .addSerializer(new ObjectIdSerializer())
                        .addSerializer(new VersionSerializer())
                        .addSerializer(new SemverSerializer())
                        .addSerializer(new SemverRequirementSerializer())
                        .addSerializer(GRN.class, new ToStringSerializer())
                        .addSerializer(EncryptedValue.class, new EncryptedValueSerializer())
                        .addDeserializer(Version.class, new VersionDeserializer())
                        .addDeserializer(Semver.class, new SemverDeserializer())
                        .addDeserializer(Requirement.class, new SemverRequirementDeserializer())
                        .addDeserializer(GRN.class, new GRNDeserializer(grnRegistry))
                        .addDeserializer(EncryptedValue.class, new EncryptedValueDeserializer(encryptedValueService))
                );

        if (subtypes != null) {
            objectMapper.registerSubtypes(subtypes.toArray(new NamedType[]{}));
        }
    }

    @Override
    public ObjectMapper get() {
        return objectMapper;
    }
}
