/*
 * */
package com.synectiks.process.server.rest.models.configuration.responses;

import java.util.Locale;
import java.util.Map;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class NumberField extends RequestedConfigurationField {

    private final static String TYPE = "number";

    public enum Attribute {
        ONLY_POSITIVE,
        ONLY_NEGATIVE,
        IS_PORT_NUMBER
    }

    public NumberField(Map.Entry<String, Map<String, Object>> c) {
        super(TYPE, c);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String attributeToJSValidation(String attribute) {
        switch (Attribute.valueOf(attribute.toUpperCase(Locale.ENGLISH))) {
            case ONLY_NEGATIVE:
                return "negative_number";
            case ONLY_POSITIVE:
                return "positive_number";
            case IS_PORT_NUMBER:
                return "port_number";
            default:
                throw new RuntimeException("No JS validation for type [" + attribute + "].");
        }
    }

}
