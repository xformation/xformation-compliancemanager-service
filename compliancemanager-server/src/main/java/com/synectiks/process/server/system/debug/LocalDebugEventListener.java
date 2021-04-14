/*
 * */
package com.synectiks.process.server.system.debug;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class LocalDebugEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(LocalDebugEventListener.class);

    @Inject
    public LocalDebugEventListener(EventBus serverEventBus) {
        checkNotNull(serverEventBus).register(this);
    }

    @Subscribe
    public void handleDebugEvent(DebugEvent event) {
        LOG.debug("Received local debug event: {}", event);
        DebugEventHolder.setLocalDebugEvent(event);
    }
}
