/*
 * */
package com.synectiks.process.server.indexer.cluster.health;

import com.google.auto.value.AutoValue;
import javax.annotation.Nullable;

@AutoValue
public abstract class PercentageWatermarkSettings implements WatermarkSettings<Double> {

    public abstract SettingsType type();

    public abstract Double low();

    public abstract Double high();

    @Nullable
    public abstract Double floodStage();

    public static class Builder {
        private SettingsType type = SettingsType.PERCENTAGE;
        private Double low;
        private Double high;
        private Double floodStage;

        public Builder() {
        }

        public PercentageWatermarkSettings.Builder low(Double low) {
            this.low = low;
            return this;
        }

        public PercentageWatermarkSettings.Builder high(Double high) {
            this.high = high;
            return this;
        }

        public PercentageWatermarkSettings.Builder floodStage(Double floodStage) {
            this.floodStage = floodStage;
            return this;
        }

        public PercentageWatermarkSettings build() {
            return new AutoValue_PercentageWatermarkSettings(type, low, high, floodStage);
        }
    }
}
