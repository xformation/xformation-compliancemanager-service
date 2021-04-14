/*
 * */
package com.synectiks.process.server.shared.events;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeadEventLoggingListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeadEventLoggingListener.class);

    @Subscribe
    public void handleDeadEvent(DeadEvent event) {
        LOGGER.debug("Received unhandled event of type <{}> from event bus <{}>", event.getEvent().getClass().getCanonicalName(),
                event.getSource().toString());
        LOGGER.debug("Dead event contents: {}", event.getEvent());
    }
}
