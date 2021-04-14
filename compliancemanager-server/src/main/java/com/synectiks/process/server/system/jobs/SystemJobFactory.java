/*
 * */
package com.synectiks.process.server.system.jobs;

import javax.inject.Inject;

import com.synectiks.process.server.indexer.healing.FixDeflectorByDeleteJob;
import com.synectiks.process.server.indexer.healing.FixDeflectorByMoveJob;

import java.util.Locale;

public class SystemJobFactory {
    private final FixDeflectorByMoveJob.Factory fixDeflectorByMoveJobFactory;
    private final FixDeflectorByDeleteJob.Factory fixDeflectorByDeleteJobFactory;

    @Inject
    public SystemJobFactory(FixDeflectorByMoveJob.Factory fixDeflectorByMoveJobFactory,
                            FixDeflectorByDeleteJob.Factory fixDeflectorByDeleteJobFactory) {
        this.fixDeflectorByMoveJobFactory = fixDeflectorByMoveJobFactory;
        this.fixDeflectorByDeleteJobFactory = fixDeflectorByDeleteJobFactory;
    }

    public SystemJob build(String jobName) throws NoSuchJobException {
        switch (SystemJob.Type.valueOf(jobName.toUpperCase(Locale.ENGLISH))) {
            case FIX_DEFLECTOR_DELETE_INDEX:
                return fixDeflectorByDeleteJobFactory.create();
            case FIX_DEFLECTOR_MOVE_INDEX:
                return fixDeflectorByMoveJobFactory.create();
        }

        throw new NoSuchJobException("Unknown system job name \"" + jobName + "\"");
    }
}
