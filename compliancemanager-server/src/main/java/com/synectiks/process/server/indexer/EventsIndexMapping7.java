/*
 * */
package com.synectiks.process.server.indexer;

import static com.synectiks.process.server.plugin.Message.FIELD_XFALERT_MESSAGE_ID;

import com.google.common.collect.ImmutableMap;

public class EventsIndexMapping7 extends EventsIndexMapping {
    @Override
    protected ImmutableMap<String, Object> fieldProperties() {
        return map()
                .putAll(super.fieldProperties())
                .put(FIELD_XFALERT_MESSAGE_ID, map()
                        .put("type", "alias")
                        .put("path", "id")
                        .build())
                .build();
    }
}
