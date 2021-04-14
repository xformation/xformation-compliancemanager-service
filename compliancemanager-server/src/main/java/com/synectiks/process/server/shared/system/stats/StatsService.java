/*
 * */
package com.synectiks.process.server.shared.system.stats;

import com.synectiks.process.server.shared.system.stats.fs.FsProbe;
import com.synectiks.process.server.shared.system.stats.fs.FsStats;
import com.synectiks.process.server.shared.system.stats.jvm.JvmProbe;
import com.synectiks.process.server.shared.system.stats.jvm.JvmStats;
import com.synectiks.process.server.shared.system.stats.network.NetworkProbe;
import com.synectiks.process.server.shared.system.stats.network.NetworkStats;
import com.synectiks.process.server.shared.system.stats.os.OsProbe;
import com.synectiks.process.server.shared.system.stats.os.OsStats;
import com.synectiks.process.server.shared.system.stats.process.ProcessProbe;
import com.synectiks.process.server.shared.system.stats.process.ProcessStats;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StatsService {
    private final FsProbe fsProbe;
    private final JvmProbe jvmProbe;
    private final NetworkProbe networkProbe;
    private final OsProbe osProbe;
    private final ProcessProbe processProbe;

    @Inject
    public StatsService(FsProbe fsProbe,
                        JvmProbe jvmProbe,
                        NetworkProbe networkProbe,
                        OsProbe osProbe,
                        ProcessProbe processProbe) {
        this.fsProbe = fsProbe;
        this.jvmProbe = jvmProbe;
        this.networkProbe = networkProbe;
        this.osProbe = osProbe;
        this.processProbe = processProbe;
    }

    public FsStats fsStats() {
        return fsProbe.fsStats();
    }

    public JvmStats jvmStats() {
        return jvmProbe.jvmStats();
    }

    public NetworkStats networkStats() {
        return networkProbe.networkStats();
    }

    public OsStats osStats() {
        return osProbe.osStats();
    }

    public ProcessStats processStats() {
        return processProbe.processStats();
    }

    public SystemStats systemStats() {
        return SystemStats.create(fsStats(), jvmStats(), networkStats(), osStats(), processStats());
    }
}
