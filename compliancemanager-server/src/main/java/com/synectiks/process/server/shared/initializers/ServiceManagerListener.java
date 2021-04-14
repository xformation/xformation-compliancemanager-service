/*
 * */
package com.synectiks.process.server.shared.initializers;

import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager.Listener;
import com.synectiks.process.server.plugin.ServerStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public class ServiceManagerListener extends Listener {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceManagerListener.class);
    private final ServerStatus serverStatus;

    @Inject
    public ServiceManagerListener(ServerStatus serverStatus) {
        this.serverStatus = serverStatus;
    }

    @Override
    public void healthy() {
        LOG.info("Services are healthy");
        serverStatus.start();
    }

    @Override
    public void stopped() {
        LOG.info("Services are now stopped.");
    }

    @Override
    public void failure(Service service) {
        // do not log the failure here again, the ServiceManager itself does so already on Level ERROR.
        serverStatus.fail();
    }
}
