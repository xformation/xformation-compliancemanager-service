/*
 * */
package com.synectiks.process.common.grn.providers;

import com.synectiks.process.common.events.notifications.DBNotificationService;
import com.synectiks.process.common.events.notifications.NotificationDto;
import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.grn.GRNDescriptor;
import com.synectiks.process.common.grn.GRNDescriptorProvider;

import javax.inject.Inject;
import java.util.Optional;

public class EventNotificationGRNDescriptorProvider implements GRNDescriptorProvider {
    private final DBNotificationService dbNotificationService;

    @Inject
    public EventNotificationGRNDescriptorProvider(DBNotificationService dbNotificationService) {
        this.dbNotificationService = dbNotificationService;
    }

    @Override
    public GRNDescriptor get(GRN grn) {
        final Optional<String> title = dbNotificationService.get(grn.entity()).map(NotificationDto::title);
        return GRNDescriptor.create(grn, title.orElse("ERROR: EventNotification for <" + grn.toString() + "> not found!"));
    }
}
