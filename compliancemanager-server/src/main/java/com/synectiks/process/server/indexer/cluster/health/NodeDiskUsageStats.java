/*
 * */
package com.synectiks.process.server.indexer.cluster.health;

import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;

@AutoValue
public abstract class NodeDiskUsageStats {
    public static final double DEFAULT_DISK_USED_PERCENT = -1D;

    public abstract String name();

    public abstract String ip();

    @Nullable
    public abstract String host();

    public abstract ByteSize diskTotal();

    public abstract ByteSize diskUsed();

    public abstract ByteSize diskAvailable();

    public abstract Double diskUsedPercent();

    public static NodeDiskUsageStats create(String name, String ip, @Nullable String host, String diskUsedString, String diskTotalString, Double diskUsedPercent) {
        ByteSize diskTotal = SIUnitParser.parseBytesSizeValue(diskTotalString);
        ByteSize diskUsed = SIUnitParser.parseBytesSizeValue(diskUsedString);
        ByteSize diskAvailable = () -> diskTotal.getBytes() - diskUsed.getBytes();
        return new AutoValue_NodeDiskUsageStats(
                name,
                ip,
                host,
                diskTotal,
                diskUsed,
                diskAvailable,
                diskUsedPercent
        );
    }
}
