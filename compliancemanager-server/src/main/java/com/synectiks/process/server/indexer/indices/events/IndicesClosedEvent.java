/*
 * */
package com.synectiks.process.server.indexer.indices.events;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Set;

@AutoValue
@WithBeanGetter
public abstract class IndicesClosedEvent {
    public abstract Set<String> indices();

    public static IndicesClosedEvent create(Set<String> indices) {
        return new AutoValue_IndicesClosedEvent(ImmutableSet.copyOf(indices));
    }

    public static IndicesClosedEvent create(String index) {
        return new AutoValue_IndicesClosedEvent(ImmutableSet.of(index));
    }
}
