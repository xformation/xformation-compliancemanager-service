/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6;

import io.searchbox.core.Index;

import com.synectiks.process.server.indexer.IndexMapping;
import com.synectiks.process.server.plugin.Message;

import java.util.Map;

public class IndexingHelper {
    public Index prepareIndexRequest(String index, Map<String, Object> source, String id) {
        source.remove(Message.FIELD_ID);

        return new Index.Builder(source)
                .index(index)
                .type(IndexMapping.TYPE_MESSAGE)
                .id(id)
                .build();
    }
}
