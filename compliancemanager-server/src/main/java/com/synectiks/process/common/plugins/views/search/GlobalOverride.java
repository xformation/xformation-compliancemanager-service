/*
 * */
package com.synectiks.process.common.plugins.views.search;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.search.engine.BackendQuery;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

import java.util.Optional;

@AutoValue
public abstract class GlobalOverride {
    public abstract Optional<TimeRange> timerange();
    public abstract Optional<BackendQuery> query();
    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_GlobalOverride.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {
        public abstract Builder timerange(TimeRange timerange);
        public abstract Builder query(BackendQuery query);
        public abstract GlobalOverride build();
    }
}
