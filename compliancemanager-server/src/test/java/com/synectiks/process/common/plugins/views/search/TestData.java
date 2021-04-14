/*
 * */
package com.synectiks.process.common.plugins.views.search;

import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.engine.BackendQuery;
import com.synectiks.process.common.plugins.views.search.views.PluginMetadataSummary;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.mock;

public class TestData {
    public static Map<String, PluginMetadataSummary> requirementsMap(String... requirementNames) {
        final Map<String, PluginMetadataSummary> requirements = new HashMap<>();

        for (String req : requirementNames)
            requirements.put(req, PluginMetadataSummary.create("", req, "", URI.create("www.affenmann.info"), "6.6.6", ""));

        return requirements;
    }

    public static Query.Builder validQueryBuilder() {
        return Query.builder().id(UUID.randomUUID().toString()).timerange(mock(TimeRange.class)).query(new BackendQuery.Fallback());
    }
}
