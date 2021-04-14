/*
 * */
package com.synectiks.process.server.contentpacks.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.synectiks.process.server.contentpacks.model.entities.TypedEntity;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class ValueReferenceTypeIdResolver extends TypeIdResolverBase {
    private final Map<String, JavaType> subtypes;

    protected ValueReferenceTypeIdResolver(JavaType baseType, TypeFactory typeFactory, Collection<NamedType> subtypes) {
        super(baseType, typeFactory);
        this.subtypes = subtypes.stream().collect(Collectors.toMap(NamedType::getName, v -> typeFactory.constructSimpleType(v.getType(), new JavaType[0])));

    }

    @Override
    public String idFromValue(Object value) {
        if (value instanceof TypedEntity) {
            final TypedEntity typedEntity = (TypedEntity) value;
            return typedEntity.typeString();
        } else {
            return null;
        }
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return null;
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        return subtypes.get(id);
    }
}
