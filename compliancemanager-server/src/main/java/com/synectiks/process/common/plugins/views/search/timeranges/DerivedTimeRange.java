/*
 * */
package com.synectiks.process.common.plugins.views.search.timeranges;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.search.GlobalOverride;
import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.SearchType;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

@AutoValue
@JsonAutoDetect
public abstract class DerivedTimeRange {
    @JsonValue
    abstract TimeRange value();

    public TimeRange effectiveTimeRange(Query query, SearchType searchType) {
        if (value() instanceof DerivableTimeRange) {
            return ((DerivableTimeRange)value()).deriveTimeRange(query, searchType);
        }

        return query.globalOverride().flatMap(GlobalOverride::timerange).orElse(value());
    }

    @JsonCreator
    public static DerivedTimeRange of(TimeRange timeRange) {
        return new AutoValue_DerivedTimeRange(timeRange);
    }
}
