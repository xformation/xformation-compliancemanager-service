/*
 * */
package com.synectiks.process.server.system.urlwhitelist;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.synectiks.process.server.notifications.Notification;
import com.synectiks.process.server.notifications.NotificationService;

@Singleton
public class UrlWhitelistNotificationService {

    private final NotificationService notificationService;

    @Inject
    public UrlWhitelistNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Publish a system notification indicating that there was an attempt to access a URL which is not whitelisted.
     *
     * <p>This method is synchronized to reduce the chance of emitting multiple notifications at the same time</p>
     *
     * @param description The description of the notification.
     */
    synchronized public void publishWhitelistFailure(String description) {
        final Notification notification = notificationService.buildNow()
                .addType(Notification.Type.GENERIC)
                .addSeverity(Notification.Severity.NORMAL)
                .addDetail("title", "URL not whitelisted.")
                .addDetail("description", description);
        notificationService.publishIfFirst(notification);
    }

}
