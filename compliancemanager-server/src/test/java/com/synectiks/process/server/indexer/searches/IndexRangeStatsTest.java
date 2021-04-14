/*
 * */
package com.synectiks.process.server.indexer.searches;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import com.synectiks.process.server.indexer.searches.IndexRangeStats;

import static org.assertj.jodatime.api.Assertions.assertThat;

public class IndexRangeStatsTest {
    @Test
    public void testCreate() throws Exception {
        DateTime min = new DateTime(2015, 1, 1, 0, 0, DateTimeZone.UTC);
        DateTime max = new DateTime(2015, 1, 3, 0, 0, DateTimeZone.UTC);
        IndexRangeStats indexRangeStats = IndexRangeStats.create(min, max);

        assertThat(indexRangeStats.min()).isEqualTo(min);
        assertThat(indexRangeStats.max()).isEqualTo(max);
    }

    @Test
    public void testEmptyInstance() throws Exception {
        assertThat(IndexRangeStats.EMPTY.min()).isEqualTo(new DateTime(0L, DateTimeZone.UTC));
        assertThat(IndexRangeStats.EMPTY.max()).isEqualTo(new DateTime(0L, DateTimeZone.UTC));
    }
}