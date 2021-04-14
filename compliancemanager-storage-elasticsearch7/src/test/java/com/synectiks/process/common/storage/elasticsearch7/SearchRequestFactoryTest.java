/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch7;

import org.graylog.shaded.elasticsearch7.org.elasticsearch.search.builder.SearchSourceBuilder;

import com.synectiks.process.common.storage.elasticsearch7.SearchRequestFactory;
import com.synectiks.process.common.storage.elasticsearch7.SortOrderMapper;
import com.synectiks.process.server.indexer.searches.ScrollCommand;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.AbsoluteRange;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static com.synectiks.process.server.utilities.AssertJsonPath.assertJsonPath;

class SearchRequestFactoryTest {
    private SearchRequestFactory searchRequestFactory;

    @BeforeEach
    void setUp() {
        this.searchRequestFactory = new SearchRequestFactory(new SortOrderMapper(), true, true);
    }

    @Test
    void searchIncludesTimerange() {
        final SearchSourceBuilder search = this.searchRequestFactory.create(ScrollCommand.builder()
                .indices(Collections.singleton("graylog_0"))
                .range(AbsoluteRange.create(
                        DateTime.parse("2020-07-23T11:03:32.243Z"),
                        DateTime.parse("2020-07-23T11:08:32.243Z")
                ))
                .build());

        assertJsonPath(search, request -> {
            request.jsonPathAsListOf("$.query.bool.filter..range.timestamp.from", String.class)
                    .containsExactly("2020-07-23 11:03:32.243");
            request.jsonPathAsListOf("$.query.bool.filter..range.timestamp.to", String.class)
                    .containsExactly("2020-07-23 11:08:32.243");
        });
    }
}
