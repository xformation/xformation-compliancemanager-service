/*
 * */
package com.synectiks.process.server.buffers;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicates;
import com.synectiks.process.server.shared.buffers.ProcessBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Buffers {
    private static final Logger LOG = LoggerFactory.getLogger(Buffers.class);
    private static final long DEFAULT_MAX_WAIT = 30L;

    private final ProcessBuffer processBuffer;
    private final OutputBuffer outputBuffer;

    @Inject
    public Buffers(final ProcessBuffer processBuffer, final OutputBuffer outputBuffer) {
        this.processBuffer = processBuffer;
        this.outputBuffer = outputBuffer;
    }

    public void waitForEmptyBuffers() {
        waitForEmptyBuffers(DEFAULT_MAX_WAIT, TimeUnit.SECONDS);
    }

    public void waitForEmptyBuffers(final long maxWait, final TimeUnit timeUnit) {
        LOG.info("Waiting until all buffers are empty.");
        final Callable<Boolean> checkForEmptyBuffers = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (processBuffer.isEmpty() && outputBuffer.isEmpty()) {
                    return true;
                } else {
                    LOG.info("Waiting for buffers to drain. ({}p/{}o)", processBuffer.getUsage(), outputBuffer.getUsage());
                }

                return false;
            }
        };

        final Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfResult(Predicates.not(Predicates.equalTo(Boolean.TRUE)))
                .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterDelay(maxWait, timeUnit))
                .build();

        try {
            retryer.call(checkForEmptyBuffers);
        } catch (RetryException e) {
            LOG.info("Buffers not empty after {} {}. Giving up.", maxWait, timeUnit.name().toLowerCase(Locale.ENGLISH));
            return;
        } catch (ExecutionException e) {
            LOG.error("Error while waiting for empty buffers.", e);
            return;
        }

        LOG.info("All buffers are empty. Continuing.");
    }
}
