/*
 * */
package com.synectiks.process.server.outputs;

import com.google.common.collect.Maps;
import com.synectiks.process.server.outputs.MessageOutputFactory;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.outputs.MessageOutput;
import com.synectiks.process.server.plugin.outputs.MessageOutputConfigurationException;
import com.synectiks.process.server.plugin.streams.Output;
import com.synectiks.process.server.plugin.streams.Stream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageOutputFactoryTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private final Map<String, MessageOutput.Factory<? extends MessageOutput>> availableOutputs;
    private final Map<String, MessageOutput.Factory2<? extends MessageOutput>> availableOutputs2;

    private MessageOutputFactory messageOutputFactory;

    public MessageOutputFactoryTest() {
        this.availableOutputs = Maps.newHashMap();
        this.availableOutputs2 = Maps.newHashMap();
    }

    @Before
    public void setUp() throws Exception {
        this.messageOutputFactory = new MessageOutputFactory(availableOutputs, availableOutputs2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonExistentOutputType() throws MessageOutputConfigurationException {
        final String outputType = "non.existent";
        final Output output = mock(Output.class);
        when(output.getType()).thenReturn(outputType);
        final Stream stream = mock(Stream.class);
        final Configuration configuration = mock(Configuration.class);

        messageOutputFactory.fromStreamOutput(output, stream, configuration);
    }
}
