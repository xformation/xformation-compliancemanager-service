/*
 * */
package com.synectiks.process.server.rest.models.configuration.responses;

import java.util.Map;

public class BooleanField extends RequestedConfigurationField {
    private final static String TYPE = "boolean";

    public BooleanField(Map.Entry<String, Map<String, Object>> c) {
        super(TYPE, c);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String attributeToJSValidation(String attribute) {
        throw new RuntimeException("This type does not have any validatable attributes.");
    }

}