/*
 * */
package com.synectiks.process.server.utilities;

import com.google.common.collect.Maps;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.database.ValidationException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigurationMapConverter {
    /**
     * Converts the values in the map to the requested types. This has been copied from the alertmanager web interface
     * and should be removed once we have better configuration objects.
     */
    public static Map<String, Object> convertValues(final Map<String, Object> data, final ConfigurationRequest configurationRequest) throws ValidationException {
        final Map<String, Object> configuration = Maps.newHashMapWithExpectedSize(data.size());
        final Map<String, Map<String, Object>> configurationFields = configurationRequest.asList();

        for (final Map.Entry<String, Object> entry : data.entrySet()) {
            final String field = entry.getKey();
            final Map<String, Object> fieldDescription = configurationFields.get(field);
            if (fieldDescription == null || fieldDescription.isEmpty()) {
                throw new ValidationException(field, "Unknown configuration field description for field \"" + field + "\"");
            }

            final String type = (String) fieldDescription.get("type");

            // Decide what to cast to. (string, bool, number)
            Object value;
            switch (type) {
                case "text":
                case "dropdown":
                    value = entry.getValue() == null ? "" : String.valueOf(entry.getValue());
                    break;
                case "number":
                    try {
                        value = Integer.parseInt(String.valueOf(entry.getValue()));
                    } catch (NumberFormatException e) {
                        // If a numeric field is optional and not provided, use null as value
                        if ("true".equals(String.valueOf(fieldDescription.get("is_optional")))) {
                            value = null;
                        } else {
                            throw new ValidationException(field, e.getMessage());
                        }
                    }
                    break;
                case "boolean":
                    value = "true".equalsIgnoreCase(String.valueOf(entry.getValue()));
                    break;
                case "list":
                    final List<?> valueList = entry.getValue() == null ? Collections.emptyList() : (List<?>) entry.getValue();
                    value = valueList.stream()
                            .filter(o -> o != null && o instanceof String)
                            .map(String::valueOf)
                            .collect(Collectors.toList());
                    break;
                default:
                    throw new ValidationException(field, "Unknown configuration field type \"" + type + "\"");
            }

            configuration.put(field, value);
        }

        return configuration;
    }
}
