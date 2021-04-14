/*
 * */
package com.synectiks.process.server.shared.system.stats;

import org.hyperic.sigar.Sigar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SigarService {
    private static final Logger LOG = LoggerFactory.getLogger(SigarService.class);
    private final Sigar sigar;

    @Inject
    public SigarService() {
        Sigar sigar = null;
        try {
            sigar = new Sigar();
            Sigar.load();
            LOG.debug("Successfully loaded SIGAR {}", Sigar.VERSION_STRING);
        } catch (Throwable t) {
            LOG.info("Failed to load SIGAR. Falling back to JMX implementations.");
            LOG.debug("Reason for SIGAR loading failure", t);

            if (sigar != null) {
                try {
                    sigar.close();
                } catch (Throwable t1) {
                    // ignore
                } finally {
                    sigar = null;
                }
            }
        }
        this.sigar = sigar;
    }

    public boolean isReady() {
        return null != sigar;
    }

    public Sigar sigar() {
        return sigar;
    }
}