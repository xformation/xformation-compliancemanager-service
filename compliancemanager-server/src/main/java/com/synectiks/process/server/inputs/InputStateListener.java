/*
 * */
package com.synectiks.process.server.inputs;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.synectiks.process.server.notifications.Notification;
import com.synectiks.process.server.notifications.NotificationService;
import com.synectiks.process.server.plugin.IOState;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.events.inputs.IOStateChangedEvent;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.shared.inputs.InputRegistry;
import com.synectiks.process.server.shared.system.activities.Activity;
import com.synectiks.process.server.shared.system.activities.ActivityWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class InputStateListener {
    private static final Logger LOG = LoggerFactory.getLogger(InputStateListener.class);
    private NotificationService notificationService;
    private ActivityWriter activityWriter;
    private ServerStatus serverStatus;

    @Inject
    public InputStateListener(EventBus eventBus,
                              NotificationService notificationService,
                              ActivityWriter activityWriter,
                              ServerStatus serverStatus) {
        this.notificationService = notificationService;
        this.activityWriter = activityWriter;
        this.serverStatus = serverStatus;
        eventBus.register(this);
    }

    @Subscribe public void inputStateChanged(IOStateChangedEvent<MessageInput> event) {
        final IOState<MessageInput> state = event.changedState();
        final MessageInput input = state.getStoppable();
        switch (event.newState()) {
            case FAILED:
                activityWriter.write(new Activity(state.getDetailedMessage(), InputRegistry.class));
                Notification notification = notificationService.buildNow();
                notification.addType(Notification.Type.INPUT_FAILED_TO_START).addSeverity(Notification.Severity.NORMAL);
                notification.addNode(serverStatus.getNodeId().toString());
                notification.addDetail("input_id", input.getId());
                notification.addDetail("reason", state.getDetailedMessage());
                notificationService.publishIfFirst(notification);
                break;
            case RUNNING:
                notificationService.fixed(Notification.Type.NO_INPUT_RUNNING);
                // fall through
            default:
                final String msg = "Input [" + input.getName() + "/" + input.getId() + "] is now " + event.newState().toString();
                activityWriter.write(new Activity(msg, InputStateListener.class));
                break;
        }

        LOG.debug("Input State of [{}/{}] changed: {} -> {}", input.getTitle(), input.getId(), event.oldState(), event.newState());
        LOG.info("Input [{}/{}] is now {}", input.getName(), input.getId(), event.newState());
    }
}
