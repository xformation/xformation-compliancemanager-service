/*
 * */
package com.synectiks.process.server.inputs.extractors;

import org.junit.Test;

import com.synectiks.process.server.ConfigurationException;
import com.synectiks.process.server.inputs.extractors.CopyInputExtractor;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.inputs.Extractor;

import static org.junit.Assert.assertEquals;

public class CopyInputExtractorTest extends AbstractExtractorTest {
    @Test
    public void testCopy() throws Extractor.ReservedFieldException, ConfigurationException {
        Message msg = new Message("The short message", "TestUnit", Tools.nowUTC());

        msg.addField("somefield", "foo");

        CopyInputExtractor x = new CopyInputExtractor(metricRegistry, "bar", "bar", 0, Extractor.CursorStrategy.COPY, "somefield", "our_result", noConfig(), "foo", noConverters(), Extractor.ConditionType.NONE, null);
        x.runExtractor(msg);

        assertEquals("foo", msg.getField("our_result"));
        assertEquals("foo", msg.getField("somefield"));
    }
}
