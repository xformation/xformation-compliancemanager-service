/*
 * */
package com.synectiks.process.server.shared.system.stats;

import com.google.inject.AbstractModule;
import com.synectiks.process.server.shared.system.stats.fs.FsProbe;
import com.synectiks.process.server.shared.system.stats.fs.JmxFsProbe;
import com.synectiks.process.server.shared.system.stats.fs.SigarFsProbe;
import com.synectiks.process.server.shared.system.stats.jvm.JvmProbe;
import com.synectiks.process.server.shared.system.stats.network.JmxNetworkProbe;
import com.synectiks.process.server.shared.system.stats.network.NetworkProbe;
import com.synectiks.process.server.shared.system.stats.network.SigarNetworkProbe;
import com.synectiks.process.server.shared.system.stats.os.JmxOsProbe;
import com.synectiks.process.server.shared.system.stats.os.OsProbe;
import com.synectiks.process.server.shared.system.stats.os.SigarOsProbe;
import com.synectiks.process.server.shared.system.stats.process.JmxProcessProbe;
import com.synectiks.process.server.shared.system.stats.process.ProcessProbe;
import com.synectiks.process.server.shared.system.stats.process.SigarProcessProbe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemStatsModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(SystemStatsModule.class);
    private final boolean disableSigar;

    public SystemStatsModule(boolean disableSigar) {
        this.disableSigar = disableSigar;
    }

    @Override
    protected void configure() {
        boolean sigarLoaded = false;

        if(disableSigar) {
            LOG.debug("SIGAR disabled. Using JMX implementations.");
        } else {
            try {
                SigarService sigarService = new SigarService();
                if (sigarService.isReady()) {
                    bind(SigarService.class).toInstance(sigarService);
                    bind(FsProbe.class).to(SigarFsProbe.class).asEagerSingleton();
                    bind(NetworkProbe.class).to(SigarNetworkProbe.class).asEagerSingleton();
                    bind(OsProbe.class).to(SigarOsProbe.class).asEagerSingleton();
                    bind(ProcessProbe.class).to(SigarProcessProbe.class).asEagerSingleton();
                    sigarLoaded = true;
                }
            } catch (Throwable e) {
                LOG.debug("Failed to load SIGAR. Falling back to JMX implementations.", e);
            }
        }

        if (!sigarLoaded) {
            bind(FsProbe.class).to(JmxFsProbe.class).asEagerSingleton();
            bind(NetworkProbe.class).to(JmxNetworkProbe.class).asEagerSingleton();
            bind(OsProbe.class).to(JmxOsProbe.class).asEagerSingleton();
            bind(ProcessProbe.class).to(JmxProcessProbe.class).asEagerSingleton();
        }

        bind(JvmProbe.class).asEagerSingleton();
    }
}