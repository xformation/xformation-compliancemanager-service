/*
 * */
package com.synectiks.process.server.lookup;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface LookupDefaultValue {
    String FIELD_VALUE_STRING = "value_string";
    String FIELD_VALUE_TYPE = "value_type";
    String FIELD_VALUE = "value";

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    enum Type {
        STRING, NUMBER, OBJECT, BOOLEAN, NULL
    }

    String valueString();
    LookupDefaultValue.Type valueType();
    Object value();
    boolean isSet();
}
