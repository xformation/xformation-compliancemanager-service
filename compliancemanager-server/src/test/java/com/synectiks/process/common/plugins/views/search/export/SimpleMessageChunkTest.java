/*
 * */
package com.synectiks.process.common.plugins.views.search.export;

import org.junit.jupiter.api.Test;

import com.synectiks.process.common.plugins.views.search.export.SimpleMessage;
import com.synectiks.process.common.plugins.views.search.export.SimpleMessageChunk;

import static com.synectiks.process.common.plugins.views.search.export.LinkedHashSetUtil.linkedHashSetOf;
import static com.synectiks.process.common.plugins.views.search.export.TestData.simpleMessage;
import static com.synectiks.process.common.plugins.views.search.export.TestData.simpleMessageChunk;
import static org.assertj.core.api.Assertions.assertThat;

class SimpleMessageChunkTest {
    @Test
    void getsValuesInOrder() {
        Object[] msg1Values = {"2015-01-01 01:00:00.000", "source-1"};
        Object[] msg2Values = {"2015-01-02 01:00:00.000", "source-2"};
        SimpleMessageChunk sut = simpleMessageChunk("timestamp,source",
                msg1Values,
                msg2Values
        );

        assertThat(sut.getAllValuesInOrder()).containsExactly(msg1Values, msg2Values);
    }

    @Test
    void valuesInOrderContainsMissingFieldsAsNull() {
        SimpleMessage msg1 = simpleMessage("timestamp,source", new Object[]{"2015-01-01 01:00:00.000", "source-1"});
        SimpleMessage msg2 = simpleMessage("timestamp", new Object[]{"2015-01-02 01:00:00.000"});

        SimpleMessageChunk sut = SimpleMessageChunk.from(
                linkedHashSetOf("timestamp", "source"),
                msg1, msg2);

        assertThat(sut.getAllValuesInOrder()).containsExactly(
                new Object[]{"2015-01-01 01:00:00.000", "source-1"},
                new Object[]{"2015-01-02 01:00:00.000", null});
    }
}
