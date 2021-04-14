/*
 * */
package com.synectiks.process.server.rest.models.alarmcallbacks.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.synectiks.process.server.rest.models.configuration.responses.BooleanField;
import com.synectiks.process.server.rest.models.configuration.responses.DropdownField;
import com.synectiks.process.server.rest.models.configuration.responses.NumberField;
import com.synectiks.process.server.rest.models.configuration.responses.RequestedConfigurationField;
import com.synectiks.process.server.rest.models.configuration.responses.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class AvailableAlarmCallbackSummaryResponse {
    private static final Logger LOG = LoggerFactory.getLogger(AvailableAlarmCallbackSummaryResponse.class);

    public String name;
    public Map<String, Map<String, Object>> requested_configuration;

    @JsonIgnore
    public List<RequestedConfigurationField> getRequestedConfiguration() {
        return extractRequestedConfiguration(requested_configuration);
    }

    public List<RequestedConfigurationField> extractRequestedConfiguration(Map<String, Map<String, Object>> config) {
        List<RequestedConfigurationField> result = Lists.newArrayList();
        List<RequestedConfigurationField> booleanFields = Lists.newArrayList();

        for (Map.Entry<String, Map<String, Object>> entry : config.entrySet()) {
            try {
                String fieldType = (String) entry.getValue().get("type");
                switch (fieldType) {
                    case "text":
                        result.add(new TextField(entry));
                        continue;
                    case "number":
                        result.add(new NumberField(entry));
                        continue;
                    case "boolean":
                        booleanFields.add(new BooleanField(entry));
                        continue;
                    case "dropdown":
                        result.add(new DropdownField(entry));
                        continue;
                    default:
                        LOG.info("Unknown field type [{}].", fieldType);
                }
            } catch (Exception e) {
                LOG.error("Skipping invalid configuration field [" + entry.getKey() + "]", e);
            }
        }

        result.addAll(booleanFields);

        return result;
    }
}
