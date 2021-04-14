/*
 * */
package com.synectiks.process.server.indexer.searches;

import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@AutoValue
@WithBeanGetter
public abstract class IndexRangeStats {
    public static final IndexRangeStats EMPTY = create(new DateTime(0L, DateTimeZone.UTC), new DateTime(0L, DateTimeZone.UTC), Collections.emptyList());

    public abstract DateTime min();

    public abstract DateTime max();

    @Nullable
    public abstract List<String> streamIds();

    public static IndexRangeStats create(DateTime min, DateTime max, @Nullable List<String> streamIds) {
        return new AutoValue_IndexRangeStats(min, max, streamIds);
    }

    public static IndexRangeStats create(DateTime min, DateTime max) {
        return create(min, max, null);
    }
}
