/*
 * */
package com.synectiks.process.server.system.debug;

import com.google.common.eventbus.Subscribe;
import com.synectiks.process.server.events.ClusterEventBus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class ClusterDebugEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterDebugEventListener.class);

    @Inject
    public ClusterDebugEventListener(ClusterEventBus clusterEventBus) {
        checkNotNull(clusterEventBus).registerClusterEventSubscriber(this);
    }

    @Subscribe
    public void handleDebugEvent(DebugEvent event) {
        LOG.debug("Received cluster debug event: {}", event);
        DebugEventHolder.setClusterDebugEvent(event);
    }
}
