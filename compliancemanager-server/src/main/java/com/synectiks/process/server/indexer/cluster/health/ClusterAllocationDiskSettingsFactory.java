/*
 * */
package com.synectiks.process.server.indexer.cluster.health;

import java.util.stream.Stream;

public class ClusterAllocationDiskSettingsFactory {

    public static ClusterAllocationDiskSettings create(boolean enabled, String low, String high, String floodStage) {
        if (!enabled) {
            return ClusterAllocationDiskSettings.create(enabled, null);
        }
        return ClusterAllocationDiskSettings.create(enabled, createWatermarkSettings(low, high, floodStage));
    }

    private static WatermarkSettings<?> createWatermarkSettings(String low, String high, String floodStage) {
        WatermarkSettings.SettingsType lowType = getType(low);
        WatermarkSettings.SettingsType highType = getType(high);
        if (Stream.of(lowType, highType).allMatch(s -> s == WatermarkSettings.SettingsType.ABSOLUTE)) {
            AbsoluteValueWatermarkSettings.Builder builder = new AbsoluteValueWatermarkSettings.Builder()
                .low(SIUnitParser.parseBytesSizeValue(low))
                .high(SIUnitParser.parseBytesSizeValue(high));
            if (!floodStage.isEmpty()) {
                builder.floodStage(SIUnitParser.parseBytesSizeValue(floodStage));
            }
            return builder.build();
        } else if (Stream.of(lowType, highType).allMatch(s -> s == WatermarkSettings.SettingsType.PERCENTAGE)) {
            PercentageWatermarkSettings.Builder builder = new PercentageWatermarkSettings.Builder()
                .low(getPercentageValue(low))
                .high(getPercentageValue(high));
            if (!floodStage.isEmpty()) {
                builder.floodStage(getPercentageValue(floodStage));
            }
            return builder.build();
        }
        throw new IllegalStateException("Error creating ClusterAllocationDiskWatermarkSettings. This should never happen.");
    }

    private static WatermarkSettings.SettingsType getType(String value) {
        if (value.trim().endsWith("%")) {
            return WatermarkSettings.SettingsType.PERCENTAGE;
        }
        return WatermarkSettings.SettingsType.ABSOLUTE;
    }

    private static Double getPercentageValue(String value) {
        return Double.parseDouble(value.trim().replace("%", ""));
    }
}
