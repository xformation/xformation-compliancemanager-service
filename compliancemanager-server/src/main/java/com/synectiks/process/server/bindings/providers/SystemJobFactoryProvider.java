/*
 * */
package com.synectiks.process.server.bindings.providers;

import com.synectiks.process.server.indexer.healing.FixDeflectorByDeleteJob;
import com.synectiks.process.server.indexer.healing.FixDeflectorByMoveJob;
import com.synectiks.process.server.system.jobs.SystemJobFactory;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public class SystemJobFactoryProvider implements Provider<SystemJobFactory> {
    private static SystemJobFactory systemJobFactory = null;

    @Inject
    public SystemJobFactoryProvider(FixDeflectorByDeleteJob.Factory deleteJobFactory,
                                    FixDeflectorByMoveJob.Factory moveJobFactory) {
        if (systemJobFactory == null)
            systemJobFactory = new SystemJobFactory(moveJobFactory, deleteJobFactory);
    }

    @Override
    public SystemJobFactory get() {
        return systemJobFactory;
    }
}
