/*
 * */
package com.synectiks.process.server.indexer.results;

import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
public abstract class CountResult {
    public abstract long count();

    public abstract long tookMs();

    public static CountResult create(long count, long tookMs) {
        return new AutoValue_CountResult(count, tookMs);
    }

    public static CountResult empty() {
        return create(0, 0);
    }
}
