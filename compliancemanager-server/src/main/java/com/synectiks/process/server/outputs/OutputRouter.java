/*
 * */
package com.synectiks.process.server.outputs;

import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.outputs.MessageOutput;
import com.synectiks.process.server.plugin.streams.Output;
import com.synectiks.process.server.plugin.streams.Stream;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

public class OutputRouter {
    private final MessageOutput defaultMessageOutput;
    private final OutputRegistry outputRegistry;

    @Inject
    public OutputRouter(@DefaultMessageOutput MessageOutput defaultMessageOutput,
                        OutputRegistry outputRegistry) {
        this.defaultMessageOutput = defaultMessageOutput;
        this.outputRegistry = outputRegistry;
    }

    protected Set<MessageOutput> getMessageOutputsForStream(Stream stream) {
        Set<MessageOutput> result = new HashSet<>();
        for (Output output : stream.getOutputs()) {
            final MessageOutput messageOutput = outputRegistry.getOutputForIdAndStream(output.getId(), stream);
            if (messageOutput != null) {
                result.add(messageOutput);
            }
        }

        return result;
    }

    public Set<MessageOutput> getOutputsForMessage(final Message msg) {
        final Set<MessageOutput> result = getStreamOutputsForMessage(msg);
        result.add(defaultMessageOutput);

        return result;
    }

    public Set<MessageOutput> getStreamOutputsForMessage(final Message msg) {
        final Set<MessageOutput> result = new HashSet<>();

        for (Stream stream : msg.getStreams()) {
            result.addAll(getMessageOutputsForStream(stream));
        }

        return result;
    }
}
