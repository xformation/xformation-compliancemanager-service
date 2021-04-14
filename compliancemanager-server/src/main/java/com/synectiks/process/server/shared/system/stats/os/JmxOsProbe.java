/*
 * */
package com.synectiks.process.server.shared.system.stats.os;

import javax.inject.Singleton;
import java.lang.management.ManagementFactory;

@Singleton
public class JmxOsProbe implements OsProbe {
    @Override
    public OsStats osStats() {
        final long uptime = -1L;
        final double systemLoadAverage = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
        final double[] loadAverage = systemLoadAverage < 0.0d ? new double[0] : new double[]{systemLoadAverage};
        final Processor processor = Processor.create("Unknown", "Unknown", -1, -1, -1, -1, -1L,
                (short) -1, (short) -1, (short) -1, (short) -1);
        final Memory memory = Memory.create(-1L, -1L, (short) -1, -1L, (short) -1, -1L, -1L);
        final Swap swap = Swap.create(-1L, -1L, -1L);

        return OsStats.create(loadAverage, uptime, processor, memory, swap);
    }
}
