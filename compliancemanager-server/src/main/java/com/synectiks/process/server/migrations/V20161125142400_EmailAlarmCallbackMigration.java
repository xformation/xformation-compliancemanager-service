/*
 * */
package com.synectiks.process.server.migrations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.annotations.VisibleForTesting;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackConfiguration;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackConfigurationService;
import com.synectiks.process.server.alarmcallbacks.EmailAlarmCallback;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.database.Persisted;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.rest.models.alarmcallbacks.requests.CreateAlarmCallbackRequest;
import com.synectiks.process.server.streams.StreamService;

import org.graylog.autovalue.WithBeanGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class V20161125142400_EmailAlarmCallbackMigration extends Migration {
    private static final Logger LOG = LoggerFactory.getLogger(V20161125142400_EmailAlarmCallbackMigration.class);

    private final ClusterConfigService clusterConfigService;
    private final StreamService streamService;
    private final AlarmCallbackConfigurationService alarmCallbackService;
    private final EmailAlarmCallback emailAlarmCallback;

    @Inject
    public V20161125142400_EmailAlarmCallbackMigration(ClusterConfigService clusterConfigService, StreamService streamService, AlarmCallbackConfigurationService alarmCallbackService, EmailAlarmCallback emailAlarmCallback) {
        this.clusterConfigService = clusterConfigService;
        this.streamService = streamService;
        this.alarmCallbackService = alarmCallbackService;
        this.emailAlarmCallback = emailAlarmCallback;
    }

    @Override
    public ZonedDateTime createdAt() {
        return ZonedDateTime.parse("2016-11-25T14:24:00Z");
    }

    private boolean hasAlertReceivers(Stream stream) {
        final Map<String, List<String>> alertReceivers = stream.getAlertReceivers();
        if (alertReceivers == null || alertReceivers.isEmpty()) {
            return false;
        }

        final List<String> users = alertReceivers.get("users");
        final List<String> emails = alertReceivers.get("emails");
        return users != null && !users.isEmpty() || emails != null && !emails.isEmpty();
    }

    @Override
    public void upgrade() {
        // Do not run again if the migration marker can be found in the database.
        if (clusterConfigService.get(MigrationCompleted.class) != null) {
            return;
        }

        final Map<String, Optional<String>> streamMigrations = this.streamService.loadAll()
                .stream()
                .filter(stream -> this.hasAlertReceivers(stream)
                        && !streamService.getAlertConditions(stream).isEmpty()
                        && alarmCallbackService.getForStream(stream).isEmpty())
                .collect(Collectors.toMap(Persisted::getId, this::migrateStream));
        final boolean allSucceeded = streamMigrations.values()
                .stream()
                .allMatch(Optional::isPresent);

        final long count = streamMigrations.size();
        if (allSucceeded) {
            if (count > 0) {
                LOG.info("Successfully migrated " + count + " streams to include explicit email alarm callback.");
            } else {
                LOG.info("No streams needed to be migrated.");
            }
            this.clusterConfigService.write(MigrationCompleted.create(streamMigrations));
        } else {
            final long errors = streamMigrations.values()
                    .stream()
                    .filter(callbackId -> !callbackId.isPresent())
                    .count();
            LOG.error("Failed migrating " + errors + "/" + count + " streams to include explicit email alarm callback.");
        }
    }

    private Optional<String> migrateStream(Stream stream) {
        final Map<String, Object> defaultConfig = this.getDefaultEmailAlarmCallbackConfig();
        LOG.debug("Creating email alarm callback for stream <" + stream.getId() + ">");
        final AlarmCallbackConfiguration alarmCallbackConfiguration = alarmCallbackService.create(stream.getId(),
                CreateAlarmCallbackRequest.create(
                        EmailAlarmCallback.class.getCanonicalName(),
                        "Email Alert Notification",
                        defaultConfig
                ),
                "local:admin"
        );
        try {
            final String callbackId = this.alarmCallbackService.save(alarmCallbackConfiguration);
            LOG.debug("Successfully created email alarm callback <" + callbackId + "> for stream <" + stream.getId() + ">.");
            return Optional.of(callbackId);
        } catch (ValidationException e) {
            LOG.error("Unable to create email alarm callback for stream <" + stream.getId() + ">: ", e);
        }
        return Optional.empty();
    }

    @VisibleForTesting
    Map<String, Object> getDefaultEmailAlarmCallbackConfig() {
        final ConfigurationRequest configurationRequest = this.emailAlarmCallback.getRequestedConfiguration();

        return configurationRequest.getFields().entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getDefaultValue()));
    }

    @AutoValue
    @WithBeanGetter
    @JsonAutoDetect
    public static abstract class MigrationCompleted {
        private static final String FIELD_CALLBACK_IDS = "callback_ids";

        @JsonProperty(FIELD_CALLBACK_IDS)
        public abstract Map<String, Optional<String>> callbackIds();

        @JsonCreator
        public static MigrationCompleted create(@JsonProperty(FIELD_CALLBACK_IDS) Map<String, Optional<String>> callbackIds) {
            return new AutoValue_V20161125142400_EmailAlarmCallbackMigration_MigrationCompleted(callbackIds);
        }
    }
}
