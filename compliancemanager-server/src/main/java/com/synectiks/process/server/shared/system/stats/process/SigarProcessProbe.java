/*
 * */
package com.synectiks.process.server.shared.system.stats.process;

import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.synectiks.process.server.shared.system.stats.SigarService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SigarProcessProbe implements ProcessProbe {
    private final SigarService sigarService;

    @Inject
    public SigarProcessProbe(SigarService sigarService) {
        this.sigarService = sigarService;
    }

    @Override
    public synchronized ProcessStats processStats() {
        final Sigar sigar = sigarService.sigar();

        final long pid = sigar.getPid();
        final long openFileDescriptors = JmxProcessProbe.getOpenFileDescriptorCount();
        final long maxFileDescriptorCount = JmxProcessProbe.getMaxFileDescriptorCount();

        ProcessStats.Cpu cpu;
        try {
            ProcCpu procCpu = sigar.getProcCpu(pid);
            cpu = ProcessStats.Cpu.create(
                    (short) (procCpu.getPercent() * 100),
                    procCpu.getSys(),
                    procCpu.getUser(),
                    procCpu.getTotal());
        } catch (SigarException e) {
            cpu = null;
        }

        ProcessStats.Memory memory;
        try {
            ProcMem mem = sigar.getProcMem(sigar.getPid());
            memory = ProcessStats.Memory.create(
                    mem.getSize(),
                    mem.getResident(),
                    mem.getShare());
        } catch (SigarException e) {
            memory = null;
        }

        return ProcessStats.create(pid, openFileDescriptors, maxFileDescriptorCount, cpu, memory);
    }
}
