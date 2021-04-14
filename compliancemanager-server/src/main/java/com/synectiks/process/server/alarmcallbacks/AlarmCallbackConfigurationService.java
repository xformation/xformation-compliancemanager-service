/*
 * */
package com.synectiks.process.server.alarmcallbacks;

import com.google.inject.ImplementedBy;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.rest.models.alarmcallbacks.requests.CreateAlarmCallbackRequest;

import java.util.List;
import java.util.Map;

@ImplementedBy(AlarmCallbackConfigurationServiceImpl.class)
public interface AlarmCallbackConfigurationService {
    List<AlarmCallbackConfiguration> getForStreamId(String streamId);
    List<AlarmCallbackConfiguration> getForStream(Stream stream);
    AlarmCallbackConfiguration load(String alarmCallbackId);
    AlarmCallbackConfiguration create(String streamId, CreateAlarmCallbackRequest request, String userId);
    long count();
    Map<String, Long> countPerType();
    String save(AlarmCallbackConfiguration model) throws ValidationException;
    int destroy(AlarmCallbackConfiguration model);
}
