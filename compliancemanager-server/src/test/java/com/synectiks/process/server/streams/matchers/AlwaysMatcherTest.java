/*
 * */
package com.synectiks.process.server.streams.matchers;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.streams.matchers.AlwaysMatcher;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class AlwaysMatcherTest {
    @Test
    public void matchAlwaysReturnsTrue() throws Exception {
        final AlwaysMatcher matcher = new AlwaysMatcher();
        assertThat(matcher.match(null, null)).isTrue();
        assertThat(matcher.match(
                new Message("Test", "source", new DateTime(2016, 9, 7, 0, 0, DateTimeZone.UTC)),
                new StreamRuleMock(Collections.singletonMap("_id", "stream-rule-id"))))
                .isTrue();
    }

}