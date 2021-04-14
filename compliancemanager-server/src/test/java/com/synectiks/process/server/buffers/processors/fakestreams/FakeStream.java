/*
 * */
package com.synectiks.process.server.buffers.processors.fakestreams;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.synectiks.process.server.plugin.outputs.MessageOutput;
import com.synectiks.process.server.streams.StreamImpl;

import java.util.List;

public class FakeStream extends StreamImpl {
    private List<MessageOutput> outputs = Lists.newArrayList();

    public FakeStream(String title) {
        super(Maps.<String, Object>newHashMap());
    }

    public void addOutput(MessageOutput output) {
        outputs.add(output);
    }
}
