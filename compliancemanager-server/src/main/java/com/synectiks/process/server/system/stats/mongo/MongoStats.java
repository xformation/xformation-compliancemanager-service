/*
 * */
package com.synectiks.process.server.system.stats.mongo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.net.HostAndPort;
import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;
import java.util.List;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class MongoStats {
    @JsonProperty
    public abstract List<HostAndPort> servers();

    @JsonProperty
    public abstract BuildInfo buildInfo();

    @JsonProperty
    @Nullable
    public abstract HostInfo hostInfo();

    @JsonProperty
    @Nullable
    public abstract ServerStatus serverStatus();

    @JsonProperty
    @Nullable
    public abstract DatabaseStats databaseStats();

    public static MongoStats create(List<HostAndPort> servers,
                                    BuildInfo buildInfo,
                                    @Nullable HostInfo hostInfo,
                                    @Nullable ServerStatus serverStatus,
                                    @Nullable DatabaseStats databaseStats) {
        return new AutoValue_MongoStats(servers, buildInfo, hostInfo, serverStatus, databaseStats);
    }
}
