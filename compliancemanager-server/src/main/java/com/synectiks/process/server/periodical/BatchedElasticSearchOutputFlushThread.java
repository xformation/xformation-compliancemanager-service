/*
 * */
package com.synectiks.process.server.periodical;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.Configuration;
import com.synectiks.process.server.outputs.BlockingBatchedESOutput;
import com.synectiks.process.server.outputs.OutputRegistry;
import com.synectiks.process.server.plugin.outputs.MessageOutput;
import com.synectiks.process.server.plugin.periodical.Periodical;

import javax.inject.Inject;

public class BatchedElasticSearchOutputFlushThread extends Periodical {
    private static final Logger LOG = LoggerFactory.getLogger(BatchedElasticSearchOutputFlushThread.class);
    private final OutputRegistry outputRegistry;
    private final Configuration configuration;

    @Inject
    public BatchedElasticSearchOutputFlushThread(OutputRegistry outputRegistry, Configuration configuration) {
        this.outputRegistry = outputRegistry;
        this.configuration = configuration;
    }

    @Override
    public boolean runsForever() {
        return false;
    }

    @Override
    public boolean stopOnGracefulShutdown() {
        return true;
    }

    @Override
    public boolean masterOnly() {
        return false;
    }

    @Override
    public boolean startOnThisNode() {
        return true;
    }

    @Override
    public boolean isDaemon() {
        return false;
    }

    @Override
    public int getInitialDelaySeconds() {
        return 0;
    }

    @Override
    public int getPeriodSeconds() {
        return configuration.getOutputFlushInterval();
    }

    @Override
    public void doRun() {
        LOG.debug("Checking for outputs to flush ...");
        for (MessageOutput output : outputRegistry.getMessageOutputs()) {
            if (output instanceof BlockingBatchedESOutput) {
                try {
                    LOG.debug("Flushing output <{}>", output);
                    ((BlockingBatchedESOutput) output).forceFlushIfTimedout();
                } catch (Exception e) {
                    LOG.error("Caught exception while trying to flush output: {}", e);
                }
            }
        }
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}
