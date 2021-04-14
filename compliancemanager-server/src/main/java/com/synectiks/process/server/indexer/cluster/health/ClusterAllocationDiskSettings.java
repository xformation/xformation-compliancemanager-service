/*
 * */
package com.synectiks.process.server.indexer.cluster.health;

import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;

@AutoValue
public abstract class ClusterAllocationDiskSettings {

    public abstract boolean ThresholdEnabled();

    @Nullable
    public abstract WatermarkSettings<?> watermarkSettings();

    public static ClusterAllocationDiskSettings create(boolean thresholdEnabled, WatermarkSettings<?> watermarkSettings) {
        return new AutoValue_ClusterAllocationDiskSettings(
                thresholdEnabled,
                watermarkSettings
        );
    }
}
