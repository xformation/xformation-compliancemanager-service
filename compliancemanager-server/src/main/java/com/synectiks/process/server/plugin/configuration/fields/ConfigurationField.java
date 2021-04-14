/*
 * */
package com.synectiks.process.server.plugin.configuration.fields;

import java.util.List;
import java.util.Map;

public interface ConfigurationField {
    int DEFAULT_POSITION = 100;  // corresponds to ConfigurationForm.jsx
    int PLACE_AT_END_POSITION = 200;

    enum Optional {
        OPTIONAL,
        NOT_OPTIONAL
    }

    String getFieldType();

    String getName();

    String getHumanName();

    String getDescription();

    Object getDefaultValue();

    void setDefaultValue(Object defaultValue);

    Optional isOptional();

    List<String> getAttributes();

    Map<String, Map<String, String>> getAdditionalInformation();

    default int getPosition() {
        return DEFAULT_POSITION;
    }
}
