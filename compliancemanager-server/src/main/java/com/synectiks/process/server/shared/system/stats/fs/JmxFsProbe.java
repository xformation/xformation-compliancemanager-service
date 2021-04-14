/*
 * */
package com.synectiks.process.server.shared.system.stats.fs;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.Configuration;
import com.synectiks.process.server.plugin.KafkaJournalConfiguration;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JmxFsProbe implements FsProbe {
    private final Set<File> locations;

    @Inject
    public JmxFsProbe(Configuration configuration, KafkaJournalConfiguration kafkaJournalConfiguration) {
        this.locations = ImmutableSet.of(
                configuration.getBinDir().toFile(),
                configuration.getDataDir().toFile(),
                kafkaJournalConfiguration.getMessageJournalDir().toFile()
        );
    }

    @Override
    public FsStats fsStats() {
        final Map<String, FsStats.Filesystem> filesystems = new HashMap<>(locations.size());

        for (File location : locations) {
            final String path = location.getAbsolutePath();
            final long total = location.getTotalSpace();
            final long free = location.getFreeSpace();
            final long available = location.getUsableSpace();
            final long used = total - free;
            final short usedPercent = (short) ((double) used / total * 100);

            final FsStats.Filesystem filesystem = FsStats.Filesystem.create(
                    path, total, free, available, used, usedPercent);

            filesystems.put(path, filesystem);
        }

        return FsStats.create(filesystems);
    }
}
