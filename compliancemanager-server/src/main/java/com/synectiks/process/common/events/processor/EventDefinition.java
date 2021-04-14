/*
 * */
package com.synectiks.process.common.events.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.synectiks.process.common.events.fields.EventFieldSpec;
import com.synectiks.process.common.events.notifications.EventNotificationHandler;
import com.synectiks.process.common.events.notifications.EventNotificationSettings;
import com.synectiks.process.common.events.processor.storage.EventStorageHandler;

import java.util.Set;

public interface EventDefinition {
    String id();

    String title();

    String description();

    int priority();

    boolean alert();

    EventProcessorConfig config();

    ImmutableMap<String, EventFieldSpec> fieldSpec();

    ImmutableList<String> keySpec();

    EventNotificationSettings notificationSettings();

    ImmutableList<EventNotificationHandler.Config> notifications();

    ImmutableList<EventStorageHandler.Config> storage();

    default Set<String> requiredPermissions() {
        return config().requiredPermissions();
    }
}
