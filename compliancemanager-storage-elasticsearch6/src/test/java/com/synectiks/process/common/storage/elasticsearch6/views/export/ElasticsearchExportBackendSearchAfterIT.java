/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6.views.export;

import static com.synectiks.process.common.storage.elasticsearch6.testing.TestUtils.jestClient;

import org.junit.Rule;
import org.junit.Test;

import com.synectiks.process.common.plugins.views.search.export.ExportMessagesCommand;
import com.synectiks.process.common.storage.elasticsearch6.testing.ElasticsearchInstanceES6;
import com.synectiks.process.common.storage.elasticsearch6.views.export.JestWrapper;
import com.synectiks.process.common.storage.elasticsearch6.views.export.RequestStrategy;
import com.synectiks.process.common.storage.elasticsearch6.views.export.SearchAfter;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchInstance;

public class ElasticsearchExportBackendSearchAfterIT extends ElasticsearchExportBackendITBase {
    @Rule
    public final ElasticsearchInstance elasticsearch = ElasticsearchInstanceES6.create();

    @Override
    protected ElasticsearchInstance elasticsearch() {
        return this.elasticsearch;
    }

    @Override
    protected RequestStrategy requestStrategy() {
        return new SearchAfter(new JestWrapper(jestClient(elasticsearch)));
    }

    @Test
    public void sortsByTimestampDescending() {
        importFixture("messages.json");

        ExportMessagesCommand command = commandBuilderWithAllStreams().build();

        runWithExpectedResult(command, "timestamp,source,message",
                "graylog_0, 2015-01-01T04:00:00.000Z, source-2, Ho",
                "graylog_0, 2015-01-01T03:00:00.000Z, source-1, Hi",
                "graylog_1, 2015-01-01T02:00:00.000Z, source-2, He",
                "graylog_0, 2015-01-01T01:00:00.000Z, source-1, Ha");
    }
}
