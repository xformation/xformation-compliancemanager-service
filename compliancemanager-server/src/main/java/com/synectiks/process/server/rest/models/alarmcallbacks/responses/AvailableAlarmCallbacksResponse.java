/*
 * */
package com.synectiks.process.server.rest.models.alarmcallbacks.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.synectiks.process.server.rest.models.configuration.responses.RequestedConfigurationField;

import java.util.List;
import java.util.Map;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public class AvailableAlarmCallbacksResponse {
    public Map<String, AvailableAlarmCallbackSummaryResponse> types;

    @JsonIgnore
    public Map<String, List<RequestedConfigurationField>> getRequestedConfiguration() {
        Map<String, List<RequestedConfigurationField>> result = Maps.newHashMap();

        for (Map.Entry<String, AvailableAlarmCallbackSummaryResponse> entry : types.entrySet()) {
            result.put(entry.getKey(), entry.getValue().extractRequestedConfiguration(entry.getValue().requested_configuration));
        }

        return result;
    }
}
