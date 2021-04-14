/*
 * */
package com.synectiks.process.server.indexer.cluster.health;

import org.junit.Test;

import com.synectiks.process.server.indexer.cluster.health.NodeDiskUsageStats;

import static org.assertj.core.api.Assertions.assertThat;

public class NodeDiskUsageStatsTest {

    private NodeDiskUsageStats nodeDiskUsageStats;

    @Test
    public void createWithValidValues() {
        nodeDiskUsageStats = NodeDiskUsageStats.create(
                "name",
                "0.0.0.0",
                "myelasticnode.graylog.org",
                "1gb",
                "20Gb",
                30.5D
        );
        assertThat(nodeDiskUsageStats.name()).isEqualTo("name");
        assertThat(nodeDiskUsageStats.ip()).isEqualTo("0.0.0.0");
        assertThat(nodeDiskUsageStats.host()).isEqualTo("myelasticnode.graylog.org");
        assertThat(nodeDiskUsageStats.diskUsed().getBytes()).isEqualTo(1073741824L);
        assertThat(nodeDiskUsageStats.diskTotal().getBytes()).isEqualTo(21474836480L);
        assertThat(nodeDiskUsageStats.diskUsedPercent()).isEqualTo(30.5D);
    }

    @Test
    public void hostCanBeNull() {
        nodeDiskUsageStats = NodeDiskUsageStats.create(
                "name",
                "0.0.0.0",
                null,
                "1mb",
                "2mb",
                99D
        );
        assertThat(nodeDiskUsageStats.host()).isNull();
    }

    @Test
    public void diskAvailabileIsCorrect() {
        nodeDiskUsageStats = NodeDiskUsageStats.create(
                "name",
                "0.0.0.0",
                null,
                "1gb",
                "3Gb",
                33.3D
        );
        assertThat(nodeDiskUsageStats.diskAvailable().getBytes()).isEqualTo(2147483648L);
    }
}
