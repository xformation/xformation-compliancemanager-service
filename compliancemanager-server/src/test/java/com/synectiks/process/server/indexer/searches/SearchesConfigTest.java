/*
 * */
package com.synectiks.process.server.indexer.searches;

import org.junit.Test;

import com.synectiks.process.server.indexer.searches.SearchesConfig;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.InvalidRangeParametersException;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.RelativeRange;

import static org.junit.Assert.assertEquals;

public class SearchesConfigTest {

    @Test
    public void defaultLimit() throws InvalidRangeParametersException {
        final SearchesConfig config = SearchesConfig.builder()
                .query("")
                .range(RelativeRange.create(5))
                .limit(0)
                .offset(0)
                .build();

        assertEquals("Limit should default", SearchesConfig.DEFAULT_LIMIT, config.limit());
    }

    @Test
    public void negativeLimit() throws InvalidRangeParametersException {
        final SearchesConfig config = SearchesConfig.builder()
                .query("")
                .range(RelativeRange.create(5))
                .limit(-100)
                .offset(0)
                .build();

        assertEquals("Limit should default", SearchesConfig.DEFAULT_LIMIT, config.limit());
    }

    @Test
    public void explicitLimit() throws InvalidRangeParametersException {
        final SearchesConfig config = SearchesConfig.builder()
                .query("")
                .range(RelativeRange.create(5))
                .limit(23)
                .offset(0)
                .build();

        assertEquals("Limit should not default", 23, config.limit());
    }

}