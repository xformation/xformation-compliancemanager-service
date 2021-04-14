/*
 * */
package com.synectiks.process.server.indexer;

import com.google.common.collect.ImmutableMap;

public class EventsIndexMapping6 extends EventsIndexMapping {
    @Override
    protected ImmutableMap<String, Object> buildMappings() {
        return map()
                .put(IndexMapping.TYPE_MESSAGE, super.buildMappings())
                .build();
    }
}
