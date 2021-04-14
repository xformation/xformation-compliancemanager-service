/*
 * */
package com.synectiks.process.common.events.notifications.types;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.events.notifications.EventNotification;
import com.synectiks.process.common.events.notifications.EventNotificationContext;
import com.synectiks.process.common.events.notifications.EventNotificationService;
import com.synectiks.process.common.events.notifications.PermanentEventNotificationException;
import com.synectiks.process.common.events.notifications.TemporaryEventNotificationException;
import com.synectiks.process.server.notifications.Notification;
import com.synectiks.process.server.notifications.NotificationService;
import com.synectiks.process.server.plugin.MessageSummary;
import com.synectiks.process.server.plugin.alarms.transports.TransportConfigurationException;
import com.synectiks.process.server.plugin.system.NodeId;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class EmailEventNotification implements EventNotification {
    public interface Factory extends EventNotification.Factory {
        @Override
        EmailEventNotification create();
    }

    private static final Logger LOG = LoggerFactory.getLogger(EmailEventNotification.class);

    private final EventNotificationService notificationCallbackService;
    private final EmailSender emailSender;
    private final NotificationService notificationService;
    private final NodeId nodeId;

    @Inject
    public EmailEventNotification(EventNotificationService notificationCallbackService,
                                  EmailSender emailSender,
                                  NotificationService notificationService,
                                  NodeId nodeId) {
        this.notificationCallbackService = notificationCallbackService;
        this.emailSender = emailSender;
        this.notificationService = notificationService;
        this.nodeId = nodeId;
    }

    @Override
    public void execute(EventNotificationContext ctx) throws TemporaryEventNotificationException, PermanentEventNotificationException {
        final EmailEventNotificationConfig config = (EmailEventNotificationConfig) ctx.notificationConfig();

        try {
            ImmutableList<MessageSummary> backlog = notificationCallbackService.getBacklogForEvent(ctx);
            emailSender.sendEmails(config, ctx, backlog);
        } catch (EmailSender.ConfigurationError e) {
            throw new TemporaryEventNotificationException(e.getMessage());
        } catch (TransportConfigurationException e) {
            Notification systemNotification = notificationService.buildNow()
                    .addNode(nodeId.toString())
                    .addType(Notification.Type.EMAIL_TRANSPORT_CONFIGURATION_INVALID)
                    .addSeverity(Notification.Severity.NORMAL)
                    .addDetail("exception", e.getMessage());
            notificationService.publishIfFirst(systemNotification);

            throw new TemporaryEventNotificationException("Notification has email recipients and is triggered, but email transport is not configured. " +
                    e.getMessage());
        } catch (Exception e) {
            String exceptionDetail = e.toString();
            if (e.getCause() != null) {
                exceptionDetail += " (" + e.getCause() + ")";
            }

            final Notification systemNotification = notificationService.buildNow()
                    .addNode(nodeId.toString())
                    .addType(Notification.Type.EMAIL_TRANSPORT_FAILED)
                    .addSeverity(Notification.Severity.NORMAL)
                    .addDetail("exception", exceptionDetail);
            notificationService.publishIfFirst(systemNotification);

            throw new PermanentEventNotificationException("Notification has email recipients and is triggered, but sending emails failed. " +
                    e.getMessage());
        }

        LOG.debug("Sending email to addresses <{}> and users <{}> using notification <{}>",
                Strings.join(config.emailRecipients(), ','),
                Strings.join(config.userRecipients(), ','),
                ctx.notificationId());
    }
}
