/*
 * */
package com.synectiks.process.server.indexer.cluster.health;

public interface WatermarkSettings<T> {

    enum SettingsType {
        ABSOLUTE,
        PERCENTAGE
    }

    SettingsType type();

    T low();

    T high();

    T floodStage();
}
