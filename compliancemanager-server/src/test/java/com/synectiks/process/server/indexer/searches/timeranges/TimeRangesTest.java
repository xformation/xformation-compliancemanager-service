/*
 * */
package com.synectiks.process.server.indexer.searches.timeranges;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import com.synectiks.process.server.indexer.searches.timeranges.TimeRanges;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.AbsoluteRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.KeywordRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.RelativeRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

import static org.assertj.core.api.Assertions.assertThat;


public class TimeRangesTest {
    @Test
    public void toSecondsHandlesIncompleteTimeRange() throws Exception {
        assertThat(TimeRanges.toSeconds(new TimeRange() {
            @Override
            public String type() {
                return AbsoluteRange.ABSOLUTE;
            }

            @Override
            public DateTime getFrom() {
                return DateTime.now(DateTimeZone.UTC);
            }

            @Override
            public DateTime getTo() {
                return null;
            }
        })).isEqualTo(0);
        assertThat(TimeRanges.toSeconds(new TimeRange() {
            @Override
            public String type() {
                return AbsoluteRange.ABSOLUTE;
            }

            @Override
            public DateTime getFrom() {
                return null;
            }

            @Override
            public DateTime getTo() {
                return DateTime.now(DateTimeZone.UTC);
            }
        })).isEqualTo(0);
    }

    @Test
    public void toSecondsReturnsCorrectNumberOfSeconds() throws Exception {
        DateTime from = DateTime.now(DateTimeZone.UTC);
        DateTime to = from.plusMinutes(5);

        assertThat(TimeRanges.toSeconds(AbsoluteRange.create(from, to))).isEqualTo(300);
        assertThat(TimeRanges.toSeconds(RelativeRange.create(300))).isEqualTo(300);
        assertThat(TimeRanges.toSeconds(KeywordRange.create("last 5 minutes"))).isEqualTo(300);
    }
}
