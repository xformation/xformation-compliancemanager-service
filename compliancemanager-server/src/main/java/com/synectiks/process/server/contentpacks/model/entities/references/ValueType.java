/*
 * */
package com.synectiks.process.server.contentpacks.model.entities.references;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.synectiks.process.server.contentpacks.jackson.ValueTypeDeserializer;
import com.synectiks.process.server.contentpacks.jackson.ValueTypeSerializer;

@JsonSerialize(using = ValueTypeSerializer.class)
@JsonDeserialize(using = ValueTypeDeserializer.class)
public enum ValueType {
    BOOLEAN(Boolean.class),
    DOUBLE(Double.class),
    FLOAT(Float.class),
    INTEGER(Integer.class),
    LONG(Long.class),
    STRING(String.class),
    PARAMETER(Void.class);

    private final Class<?> targetClass;

    ValueType(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public Class<?> targetClass() {
        return targetClass;
    }
}