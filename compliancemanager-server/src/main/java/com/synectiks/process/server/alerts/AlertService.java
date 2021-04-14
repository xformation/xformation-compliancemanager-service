/*
 * */
package com.synectiks.process.server.alerts;

import org.joda.time.DateTime;

import com.synectiks.process.server.alerts.Alert.AlertState;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.plugin.alarms.AlertCondition;
import com.synectiks.process.server.plugin.configuration.ConfigurationException;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.rest.models.streams.alerts.requests.CreateConditionRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AlertService {
    Alert factory(AlertCondition.CheckResult checkResult);

    List<Alert> loadRecentOfStreams(List<String> streamIds, DateTime since, int limit);
    List<Alert> loadRecentOfStream(String streamId, DateTime since, int limit);

    Optional<Alert> getLastTriggeredAlert(String streamId, String conditionId);

    long totalCount();
    long totalCountForStream(String streamId);
    long totalCountForStreams(List<String> streamIds, AlertState state);

    AlertCondition fromPersisted(Map<String, Object> conditionFields, Stream stream) throws ConfigurationException;
    AlertCondition fromRequest(CreateConditionRequest ccr, Stream stream, String userId) throws ConfigurationException;

    AlertCondition updateFromRequest(AlertCondition alertCondition, CreateConditionRequest ccr) throws ConfigurationException;

    boolean inGracePeriod(AlertCondition alertCondition);
    boolean shouldRepeatNotifications(AlertCondition alertCondition, Alert alert);

    List<Alert> listForStreamId(String streamId, int skip, int limit);
    List<Alert> listForStreamIds(List<String> streamIds, AlertState state, int skip, int limit);
    Alert load(String alertId, String streamId) throws NotFoundException;
    String save(Alert alert) throws ValidationException;

    Alert resolveAlert(Alert alert);
    boolean isResolved(Alert alert);
}
