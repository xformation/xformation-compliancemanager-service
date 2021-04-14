/*
 * */
package com.synectiks.process.server.indexer.cluster.health;

import org.junit.Test;

import com.synectiks.process.server.indexer.cluster.health.AbsoluteValueWatermarkSettings;
import com.synectiks.process.server.indexer.cluster.health.ByteSize;
import com.synectiks.process.server.indexer.cluster.health.ClusterAllocationDiskSettings;
import com.synectiks.process.server.indexer.cluster.health.ClusterAllocationDiskSettingsFactory;
import com.synectiks.process.server.indexer.cluster.health.PercentageWatermarkSettings;
import com.synectiks.process.server.indexer.cluster.health.WatermarkSettings;

import static org.assertj.core.api.Assertions.assertThat;

public class ClusterAllocationDiskSettingsFactoryTest {

    @Test
    public void createPercentageWatermarkSettings() throws Exception {
        ClusterAllocationDiskSettings settings = ClusterAllocationDiskSettingsFactory.create(true, "75%", "85%", "99%");

        assertThat(settings).isInstanceOf(ClusterAllocationDiskSettings.class);
        assertThat(settings.ThresholdEnabled()).isTrue();
        assertThat(settings.watermarkSettings()).isInstanceOf(PercentageWatermarkSettings.class);
        assertThat(settings.watermarkSettings().type()).isEqualTo(WatermarkSettings.SettingsType.PERCENTAGE);
        assertThat(settings.watermarkSettings().low()).isEqualTo(75D);
        assertThat(settings.watermarkSettings().high()).isEqualTo(85D);
        assertThat(settings.watermarkSettings().floodStage()).isEqualTo(99D);
    }

    @Test
    public void createPercentageWatermarkSettingsWithoutFloodStage() throws Exception {
        ClusterAllocationDiskSettings settings = ClusterAllocationDiskSettingsFactory.create(true, "65%", "75%", "");

        assertThat(settings).isInstanceOf(ClusterAllocationDiskSettings.class);
        assertThat(settings.ThresholdEnabled()).isTrue();
        assertThat(settings.watermarkSettings()).isInstanceOf(PercentageWatermarkSettings.class);
        assertThat(settings.watermarkSettings().type()).isEqualTo(WatermarkSettings.SettingsType.PERCENTAGE);
        assertThat(settings.watermarkSettings().low()).isEqualTo(65D);
        assertThat(settings.watermarkSettings().high()).isEqualTo(75D);
        assertThat(settings.watermarkSettings().floodStage()).isNull();
    }

    @Test
    public void createAbsoluteValueWatermarkSettings() throws Exception {
        ClusterAllocationDiskSettings clusterAllocationDiskSettings = ClusterAllocationDiskSettingsFactory.create(true, "20Gb", "10Gb", "5Gb");

        assertThat(clusterAllocationDiskSettings).isInstanceOf(ClusterAllocationDiskSettings.class);
        assertThat(clusterAllocationDiskSettings.ThresholdEnabled()).isTrue();
        assertThat(clusterAllocationDiskSettings.watermarkSettings()).isInstanceOf(AbsoluteValueWatermarkSettings.class);

        AbsoluteValueWatermarkSettings settings = (AbsoluteValueWatermarkSettings) clusterAllocationDiskSettings.watermarkSettings();

        assertThat(settings.type()).isEqualTo(WatermarkSettings.SettingsType.ABSOLUTE);
        assertThat(settings.low()).isInstanceOf(ByteSize.class);
        assertThat(settings.low().getBytes()).isEqualTo(21474836480L);
        assertThat(settings.high()).isInstanceOf(ByteSize.class);
        assertThat(settings.high().getBytes()).isEqualTo(10737418240L);
        assertThat(settings.floodStage()).isInstanceOf(ByteSize.class);
        assertThat(settings.floodStage().getBytes()).isEqualTo(5368709120L);
    }

    @Test
    public void createAbsoluteValueWatermarkSettingsWithoutFloodStage() throws Exception {
        ClusterAllocationDiskSettings clusterAllocationDiskSettings = ClusterAllocationDiskSettingsFactory.create(true, "10Gb", "5Gb", "");

        assertThat(clusterAllocationDiskSettings).isInstanceOf(ClusterAllocationDiskSettings.class);
        assertThat(clusterAllocationDiskSettings.ThresholdEnabled()).isTrue();
        assertThat(clusterAllocationDiskSettings.watermarkSettings()).isInstanceOf(AbsoluteValueWatermarkSettings.class);

        AbsoluteValueWatermarkSettings settings = (AbsoluteValueWatermarkSettings) clusterAllocationDiskSettings.watermarkSettings();

        assertThat(settings.type()).isEqualTo(WatermarkSettings.SettingsType.ABSOLUTE);
        assertThat(settings.low()).isInstanceOf(ByteSize.class);
        assertThat(settings.low().getBytes()).isEqualTo(10737418240L);
        assertThat(settings.high()).isInstanceOf(ByteSize.class);
        assertThat(settings.high().getBytes()).isEqualTo(5368709120L);
        assertThat(settings.floodStage()).isNull();
    }

    @Test
    public void createWithoutSettingsWhenThresholdDisabled() throws Exception {
        ClusterAllocationDiskSettings settings = ClusterAllocationDiskSettingsFactory.create(false, "", "", "");

        assertThat(settings).isInstanceOf(ClusterAllocationDiskSettings.class);
        assertThat(settings.ThresholdEnabled()).isFalse();
    }

    @Test(expected = Exception.class)
    public void throwExceptionWhenMixedSettings() throws Exception {
        ClusterAllocationDiskSettingsFactory.create(true, "10Gb", "10%", "");
    }
}
