/*
 * */
package com.synectiks.process.server.indexer.indices.events;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Set;

@AutoValue
@WithBeanGetter
public abstract class IndicesReopenedEvent {
    public abstract Set<String> indices();

    public static IndicesReopenedEvent create(Set<String> indices) {
        return new AutoValue_IndicesReopenedEvent(ImmutableSet.copyOf(indices));
    }

    public static IndicesReopenedEvent create(String index) {
        return new AutoValue_IndicesReopenedEvent(ImmutableSet.of(index));
    }
}
