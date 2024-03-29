/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch7.views.migrations;

import com.google.common.collect.ImmutableMap;
import com.synectiks.process.common.plugins.views.migrations.V20200730000000_AddGl2MessageIdFieldAliasForEvents;
import com.synectiks.process.common.storage.elasticsearch7.ElasticsearchClient;

import org.graylog.shaded.elasticsearch7.org.elasticsearch.action.support.IndicesOptions;
import org.graylog.shaded.elasticsearch7.org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.graylog.shaded.elasticsearch7.org.elasticsearch.client.indices.PutMappingRequest;

import com.synectiks.process.server.indexer.ElasticsearchException;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Set;

import static com.synectiks.process.server.plugin.Message.FIELD_XFCOMPLIANCE_MESSAGE_ID;

public class V20200730000000_AddGl2MessageIdFieldAliasForEventsES7 implements V20200730000000_AddGl2MessageIdFieldAliasForEvents.ElasticsearchAdapter {

    private final ElasticsearchClient client;

    @Inject
    public V20200730000000_AddGl2MessageIdFieldAliasForEventsES7(ElasticsearchClient client) {
        this.client = client;
    }

    @Override
    public void addXfComplianceMessageIdFieldAlias(Set<String> indexPrefixes) {

        final String[] prefixesWithWildcard = indexPrefixes.stream().map(p -> p + "*").toArray(String[]::new);

        final PutMappingRequest putMappingRequest = new PutMappingRequest(prefixesWithWildcard)
                .indicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN_CLOSED)
                .source(ImmutableMap.of("properties", ImmutableMap.of(FIELD_XFCOMPLIANCE_MESSAGE_ID, aliasMapping())));

        try {
            final AcknowledgedResponse acknowledgedResponse = client.execute((c, requestOptions) -> c.indices().putMapping(putMappingRequest, requestOptions));
            if (!acknowledgedResponse.isAcknowledged()) {
                throw new ElasticsearchException(errorMsgFor(prefixesWithWildcard) + " Elasticsearch failed to acknowledge.");
            }
        } catch (ElasticsearchException e) {
            throw new ElasticsearchException(errorMsgFor(prefixesWithWildcard), e);
        }
    }

    private String errorMsgFor(String[] prefixesWithWildcard) {
        return "Failed to add field alias " + FIELD_XFCOMPLIANCE_MESSAGE_ID + " for indices " + Arrays.toString(prefixesWithWildcard) + ".";
    }

    static LinkedHashMap<String, Object> aliasMapping() {
        LinkedHashMap<String, Object> aliasMapping = new LinkedHashMap<>();
        aliasMapping.put("type", "alias");
        aliasMapping.put("path", "id");
        return aliasMapping;
    }
}
