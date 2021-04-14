/*
 * */
package com.synectiks.process.server.rest.models.configuration.responses;

import java.util.Map;

public class DropdownField extends RequestedConfigurationField {
    private final static String TYPE = "dropdown";

    private final Map<String, String> values;

    public DropdownField(Map.Entry<String, Map<String, Object>> c) {
        super(TYPE, c);

        // lolwut
        this.values = (Map<String, String>) ((Map<String, Object>) c.getValue().get("additional_info")).get("values");
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String attributeToJSValidation(String attribute) {
        throw new RuntimeException("This type does not have any validatable attributes.");
    }

    public Map<String, String> getValues() {
        return values;
    }
}
