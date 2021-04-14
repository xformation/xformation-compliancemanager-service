/*
 * */
package com.synectiks.process.server.indexer.indices.events;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Set;

@AutoValue
@WithBeanGetter
public abstract class IndicesDeletedEvent {
    public abstract Set<String> indices();

    public static IndicesDeletedEvent create(Set<String> indices) {
        return new AutoValue_IndicesDeletedEvent(ImmutableSet.copyOf(indices));
    }

    public static IndicesDeletedEvent create(String index) {
        return new AutoValue_IndicesDeletedEvent(ImmutableSet.of(index));
    }
}
