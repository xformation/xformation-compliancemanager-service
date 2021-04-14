/*
 * */
package com.synectiks.process.server.contentpacks.exceptions;

import static java.util.Objects.requireNonNull;

import com.synectiks.process.server.contentpacks.model.entities.references.ValueType;

public class InvalidParameterTypeException extends ContentPackException {
    private final ValueType expectedValueType;
    private final ValueType actualValueType;

    public InvalidParameterTypeException(ValueType expectedValueType, ValueType actualValueType) {
        super("Incompatible value types, content pack expected " + expectedValueType + ", parameters provided " + actualValueType);
        this.expectedValueType = requireNonNull(expectedValueType, "expectedValueType");
        this.actualValueType = requireNonNull(actualValueType, "actualValueType");
    }

    public ValueType getExpectedValueType() {
        return expectedValueType;
    }

    public ValueType getActualValueType() {
        return actualValueType;
    }
}
