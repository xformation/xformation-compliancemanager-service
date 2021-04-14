/*
 * */
package com.synectiks.process.server.shared.bindings.providers;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Set;

public class ServiceManagerProvider implements Provider<ServiceManager> {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceManagerProvider.class);

    @Inject
    Set<Service> services = Sets.<Service>newHashSet(new AbstractService() {
        @Override
        protected void doStart() {
        }

        @Override
        protected void doStop() {

        }
    });

    @Override
    public ServiceManager get() {
        LOG.debug("Using services: {}", services);
        return new ServiceManager(services);
    }
}
