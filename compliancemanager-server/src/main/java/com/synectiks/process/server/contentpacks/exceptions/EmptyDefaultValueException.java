/*
 * */
package com.synectiks.process.server.contentpacks.exceptions;

public class EmptyDefaultValueException extends ContentPackException {
    private final String parameterName;

    public EmptyDefaultValueException(String parameterName) {
        super("Empty default value for missing parameter " + parameterName);
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return parameterName;
    }
}
