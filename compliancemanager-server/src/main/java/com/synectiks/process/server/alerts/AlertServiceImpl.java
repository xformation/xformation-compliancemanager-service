/*
 * */
package com.synectiks.process.server.alerts;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackHistory;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackHistoryService;
import com.synectiks.process.server.alerts.Alert.AlertState;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.CollectionName;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.alarms.AlertCondition;
import com.synectiks.process.server.plugin.configuration.ConfigurationException;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.rest.models.streams.alerts.requests.CreateConditionRequest;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Seconds;
import org.mongojack.DBQuery;
import org.mongojack.DBSort;
import org.mongojack.JacksonDBCollection;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public class AlertServiceImpl implements AlertService {
    private final JacksonDBCollection<AlertImpl, String> coll;
    private final AlertConditionFactory alertConditionFactory;
    private final AlarmCallbackHistoryService alarmCallbackHistoryService;

    @Inject
    public AlertServiceImpl(MongoConnection mongoConnection,
                            MongoJackObjectMapperProvider mapperProvider,
                            AlertConditionFactory alertConditionFactory,
                            AlarmCallbackHistoryService alarmCallbackHistoryService) {
        this.alertConditionFactory = alertConditionFactory;
        this.alarmCallbackHistoryService = alarmCallbackHistoryService;
        final String collectionName = AlertImpl.class.getAnnotation(CollectionName.class).value();
        final DBCollection dbCollection = mongoConnection.getDatabase().getCollection(collectionName);

        dbCollection.createIndex(new BasicDBObject(ImmutableMap.of(
            AlertImpl.FIELD_TRIGGERED_AT, -1,
            AlertImpl.FIELD_STREAM_ID, 1
        )));

        this.coll = JacksonDBCollection.wrap(dbCollection, AlertImpl.class, String.class, mapperProvider.get());
    }

    @Override
    public Alert factory(AlertCondition.CheckResult checkResult) {
        checkArgument(checkResult.isTriggered(), "Unable to create alert for CheckResult which is not triggered.");
        return AlertImpl.fromCheckResult(checkResult);
    }

    @Override
    public List<Alert> loadRecentOfStreams(List<String> streamIds, DateTime since, int limit) {
        if (streamIds == null || streamIds.isEmpty()) {
            return Collections.emptyList();
        }

        final DateTime effectiveSince = (since == null ? new DateTime(0L, DateTimeZone.UTC) : since);
        final DBQuery.Query query = DBQuery.and(
                getFindAnyStreamQuery(streamIds),
                DBQuery.greaterThanEquals(AlertImpl.FIELD_TRIGGERED_AT, effectiveSince)
        );

        return Collections.unmodifiableList(this.coll.find(query)
                .limit(limit)
                .sort(DBSort.desc(AlertImpl.FIELD_TRIGGERED_AT))
                .toArray());
    }

    @Override
    public List<Alert> loadRecentOfStream(String streamId, DateTime since, int limit) {
        return loadRecentOfStreams(ImmutableList.of(streamId), since, limit);
    }

    @VisibleForTesting
    int resolvedSecondsAgo(String streamId, String conditionId) {
        final Optional<Alert> lastTriggeredAlert = getLastTriggeredAlert(streamId, conditionId);
        if (!lastTriggeredAlert.isPresent()) {
            return -1;
        }

        final Alert mostRecentAlert = lastTriggeredAlert.get();

        final DateTime resolvedAt = mostRecentAlert.getResolvedAt();
        if (resolvedAt == null || !isResolved(mostRecentAlert)) {
            return -1;
        }
        return Seconds.secondsBetween(resolvedAt, Tools.nowUTC()).getSeconds();
    }

    @Override
    public Optional<Alert> getLastTriggeredAlert(String streamId, String conditionId) {
        final List<AlertImpl> alert = this.coll.find(
                DBQuery.and(
                        DBQuery.is(AlertImpl.FIELD_STREAM_ID, streamId),
                        DBQuery.is(AlertImpl.FIELD_CONDITION_ID, conditionId)
                )
        )
                .sort(DBSort.desc(AlertImpl.FIELD_TRIGGERED_AT))
                .limit(1)
                .toArray();

        if (alert == null || alert.size() == 0) {
            return Optional.empty();
        }

        return Optional.of(alert.get(0));
    }

    @Override
    public long totalCount() {
        return this.coll.count();
    }

    @Override
    public long totalCountForStream(String streamId) {
        return totalCountForStreams(ImmutableList.of(streamId), AlertState.ANY);
    }

    @Override
    public long totalCountForStreams(List<String> streamIds, AlertState state) {
        if (streamIds == null || streamIds.isEmpty()) {
            return 0;
        }

        DBQuery.Query query = getFindAnyStreamQuery(streamIds);

        if (state != null && state != AlertState.ANY) {
            query = DBQuery.and(query, getFindByStateQuery(state));
        }

        return this.coll.count(this.coll.serializeQuery(query));
    }

    @Override
    public AlertCondition fromPersisted(Map<String, Object> fields, Stream stream) throws ConfigurationException {
        final String type = (String)fields.get("type");

        return this.alertConditionFactory.createAlertCondition(type,
            stream,
            (String) fields.get("id"),
            DateTime.parse((String) fields.get("created_at")),
            (String) fields.get("creator_user_id"),
            (Map<String, Object>) fields.get("parameters"),
            (String) fields.get("title"));
    }

    @Override
    public AlertCondition fromRequest(CreateConditionRequest ccr, Stream stream, String userId) throws ConfigurationException {
        final String type = ccr.type();
        checkArgument(type != null, "Missing alert condition type");

        return this.alertConditionFactory.createAlertCondition(type, stream, null, Tools.nowUTC(), userId, ccr.parameters(), ccr.title());
    }

    @Override
    public AlertCondition updateFromRequest(AlertCondition alertCondition, CreateConditionRequest ccr) throws ConfigurationException {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.putAll(alertCondition.getParameters());
        parameters.putAll(ccr.parameters());

        return this.alertConditionFactory.createAlertCondition(
            alertCondition.getType(),
            alertCondition.getStream(),
            alertCondition.getId(),
            alertCondition.getCreatedAt(),
            alertCondition.getCreatorUserId(),
            parameters,
            ccr.title()
        );
    }

    @Override
    public boolean inGracePeriod(AlertCondition alertCondition) {
        int lastAlertSecondsAgo = resolvedSecondsAgo(alertCondition.getStream().getId(), alertCondition.getId());

        if (lastAlertSecondsAgo == -1 || alertCondition.getGrace() == 0) {
            return false;
        }

        return lastAlertSecondsAgo < alertCondition.getGrace() * 60;
    }

    @Override
    public boolean shouldRepeatNotifications(AlertCondition alertCondition, Alert alert) {
        // Do not repeat notifications if alert has no state, is resolved or the option to repeat notifications is disabled
        if (!alert.isInterval() || isResolved(alert) || !alertCondition.shouldRepeatNotifications()) {
            return false;
        }

        // Repeat notifications if no grace period is set, avoiding looking through the notification history
        if (alertCondition.getGrace() == 0) {
            return true;
        }

        AlarmCallbackHistory lastTriggeredAlertHistory = null;
        for (AlarmCallbackHistory history : alarmCallbackHistoryService.getForAlertId(alert.getId())) {
            if (lastTriggeredAlertHistory == null || lastTriggeredAlertHistory.createdAt().isBefore(history.createdAt())) {
                lastTriggeredAlertHistory = history;
            }
        }

        // Repeat notifications if no alert was ever triggered for this condition
        if (lastTriggeredAlertHistory == null) {
            return true;
        }

        final int lastAlertSecondsAgo = Seconds.secondsBetween(lastTriggeredAlertHistory.createdAt(), Tools.nowUTC()).getSeconds();

        return lastAlertSecondsAgo >= alertCondition.getGrace() * 60;
    }

    @Override
    public List<Alert> listForStreamIds(List<String> streamIds, AlertState state, int skip, int limit) {
        if (streamIds == null || streamIds.isEmpty()) {
            return Collections.emptyList();
        }

        DBQuery.Query query = getFindAnyStreamQuery(streamIds);

        if (state != null && state != AlertState.ANY) {
            query = DBQuery.and(query, getFindByStateQuery(state));
        }

        return Collections.unmodifiableList(this.coll.find(query)
                .sort(DBSort.desc(AlertImpl.FIELD_TRIGGERED_AT))
                .skip(skip)
                .limit(limit)
                .toArray());
    }

    @Override
    public List<Alert> listForStreamId(String streamId, int skip, int limit) {
        return listForStreamIds(ImmutableList.of(streamId), AlertState.ANY, skip, limit);
    }

    @Override
    public Alert load(String alertId, String streamId) throws NotFoundException {
        return this.coll.findOneById(alertId);
    }

    @Override
    public String save(Alert alert) throws ValidationException {
        checkArgument(alert instanceof AlertImpl, "Supplied argument must be of type " + AlertImpl.class + ", and not " + alert.getClass());

        return this.coll.save((AlertImpl)alert).getSavedId();
    }

    @Override
    public Alert resolveAlert(Alert alert) {
        if (alert == null || isResolved(alert)) {
            return alert;
        }

        final AlertImpl updatedAlert = ((AlertImpl) alert).toBuilder().resolvedAt(Tools.nowUTC()).build();
        this.coll.save(updatedAlert);

        return updatedAlert;
    }

    @Override
    public boolean isResolved(Alert alert) {
        return !alert.isInterval() || alert.getResolvedAt() != null;
    }

    private DBQuery.Query getFindAnyStreamQuery(List<String> streamIds) {
        final List<DBQuery.Query> streamQueries = streamIds.stream()
                .map(streamId -> DBQuery.is(AlertImpl.FIELD_STREAM_ID, streamId))
                .collect(Collectors.toList());
        return DBQuery.or(streamQueries.toArray(new DBQuery.Query[streamQueries.size()]));
    }

    private DBQuery.Query getFindByStateQuery(AlertState state) {
        if (state == AlertState.RESOLVED) {
            /* Resolved alerts:
             * - Not interval (legacy)
             * - Interval alerts with non-null resolved_at field
             */
            return DBQuery.or(
                    DBQuery.notEquals(AlertImpl.FIELD_IS_INTERVAL, true),
                    DBQuery.notEquals(AlertImpl.FIELD_RESOLVED_AT, null)
            );
        }

        if (state == AlertState.UNRESOLVED) {
            /* Unresolved alerts:
             * - Interval alerts with null resolved_at field
             */
            return DBQuery.and(
                    DBQuery.is(AlertImpl.FIELD_IS_INTERVAL, true),
                    DBQuery.is(AlertImpl.FIELD_RESOLVED_AT, null)
            );
        }

        return DBQuery.empty();
    }
}
