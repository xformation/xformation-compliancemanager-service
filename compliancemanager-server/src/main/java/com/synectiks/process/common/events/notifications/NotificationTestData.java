/*
 * */
package com.synectiks.process.common.events.notifications;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.events.contentpack.entities.EventProcessorConfigEntity;
import com.synectiks.process.common.events.event.EventDto;
import com.synectiks.process.common.events.event.EventOriginContext;
import com.synectiks.process.common.events.processor.EventDefinitionDto;
import com.synectiks.process.common.events.processor.EventProcessorConfig;
import com.synectiks.process.server.contentpacks.EntityDescriptorIds;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.rest.ValidationResult;
import com.synectiks.process.server.plugin.streams.Stream;

public class NotificationTestData {
    public static final String TEST_NOTIFICATION_ID = "NotificationTestId";

    public static EventNotificationContext getDummyContext(NotificationDto notificationDto, String userName) {
        final EventDto eventDto = EventDto.builder()
                .alert(true)
                .eventDefinitionId("EventDefinitionTestId")
                .eventDefinitionType("notification-test-v1")
                .eventTimestamp(Tools.nowUTC())
                .processingTimestamp(Tools.nowUTC())
                .id("TEST_NOTIFICATION_ID")
                .streams(ImmutableSet.of(Stream.DEFAULT_EVENTS_STREAM_ID))
                .message("Notification test message triggered from user <" + userName + ">")
                .source(Stream.DEFAULT_STREAM_ID)
                .keyTuple(ImmutableList.of("testkey"))
                .key("testkey")
                .originContext(EventOriginContext.elasticsearchMessage("testIndex_42", "b5e53442-12bb-4374-90ed-0deadbeefbaz"))
                .priority(2)
                .fields(ImmutableMap.of("field1", "value1", "field2", "value2"))
                .build();

        final EventDefinitionDto eventDefinitionDto = EventDefinitionDto.builder()
                .alert(true)
                .id(TEST_NOTIFICATION_ID)
                .title("Event Definition Test Title")
                .description("Event Definition Test Description")
                .config(new EventProcessorConfig() {
                    @Override
                    public String type() {
                        return "test-dummy-v1";
                    }
                    @Override
                    public ValidationResult validate() {
                        return null;
                    }
                    @Override
                    public EventProcessorConfigEntity toContentPackEntity(EntityDescriptorIds entityDescriptorIds) {
                        return null;
                    }
                })
                .fieldSpec(ImmutableMap.of())
                .priority(2)
                .keySpec(ImmutableList.of())
                .notificationSettings(new EventNotificationSettings() {
                                          @Override
                                          public long gracePeriodMs() {
                                              return 0;
                                          }
                                          @Override
                                          // disable to avoid errors in getBacklogForEvent()
                                          public long backlogSize() {
                                              return 0;
                                          }
                                          @Override
                                          public Builder toBuilder() {
                                              return null;
                                          }
                                      }

                ).build();

        return EventNotificationContext.builder()
                .notificationId(TEST_NOTIFICATION_ID)
                .notificationConfig(notificationDto.config())
                .event(eventDto)
                .eventDefinition(eventDefinitionDto)
                .build();
    }
}
