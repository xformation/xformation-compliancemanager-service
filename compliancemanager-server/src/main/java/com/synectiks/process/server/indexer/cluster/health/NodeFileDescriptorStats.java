/*
 * */
package com.synectiks.process.server.indexer.cluster.health;

import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;
import java.util.Optional;

@AutoValue
public abstract class NodeFileDescriptorStats {
    public abstract String name();

    public abstract String ip();

    @Nullable
    public abstract String host();

    public abstract Optional<Long> fileDescriptorMax();

    public static NodeFileDescriptorStats create(String name, String ip, @Nullable String host, Long fileDescriptorMax) {
        return new AutoValue_NodeFileDescriptorStats(name, ip, host, Optional.ofNullable(fileDescriptorMax));
    }
}
