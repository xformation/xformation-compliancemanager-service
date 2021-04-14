/*
 * */
package com.synectiks.process.server.utilities;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Service;
import com.synectiks.process.server.shared.utilities.ExceptionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * This class is a convenience listener for {@link Service} instances so that service lifecycle changes can easily
 * be logged without having to manually write listeners everywhere.
 * <br />
 * It allows adding an action that is run when the service transitions into {@link Service.State#RUNNING} as that is
 * the most common case. If you need additional, or more complex listeners, add a new instance in addition to this simple
 * class.
 */
public class LoggingServiceListener extends Service.Listener {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingServiceListener.class);

    private final String serviceName;
    private String id;
    @Nullable
    private Runnable action;
    private final Logger logger;

    /**
     * Creates a new logging listener for the service name and an id.
     * @param serviceName the concise name describing the service being listened to (e.g. "Input" or "Cache")
     * @param id the unique id of the service instance, useful if there are more than one running.
     */
    public LoggingServiceListener(String serviceName, String id) {
        this(serviceName, id, null, LOG);
    }

    public LoggingServiceListener(String serviceName, String id, @NotNull Logger logger) {
        this(serviceName, id, null, logger);
    }

    public LoggingServiceListener(String serviceName, String id, @Nullable Runnable action, @NotNull Logger logger) {
        this.serviceName = serviceName;
        this.id = id;
        this.action = action;
        this.logger = Preconditions.checkNotNull(logger);
    }

    @Override
    public void starting() {
        logger.info("{} {} STARTING", serviceName, id);
    }

    @Override
    public void running() {
        logger.info("{} {} RUNNING", serviceName, id);
        if (action != null) {
            action.run();
        }
    }

    @Override
    public void stopping(Service.State from) {
        logger.info("{} {} STOPPING, was {}", serviceName, id, from);
    }

    @Override
    public void terminated(Service.State from) {
        logger.info("{} {} TERMINATED, was {}", serviceName, id, from);
    }

    @Override
    public void failed(Service.State from, Throwable failure) {
        logger.info("{} {} FAILED, was {}: {}", serviceName, id, from, ExceptionUtils.getRootCauseMessage(failure));
    }
}
