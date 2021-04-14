/*
 * */
package com.synectiks.process.server.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.filters.MessageFilter;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.streams.StreamRouter;

import javax.inject.Inject;
import java.util.List;

public class StreamMatcherFilter implements MessageFilter {

    private static final Logger LOG = LoggerFactory.getLogger(StreamMatcherFilter.class);

    private final StreamRouter streamRouter;

    @Inject
    public StreamMatcherFilter(StreamRouter streamRouter) {
        this.streamRouter = streamRouter;
    }

    @Override
    public boolean filter(Message msg) {
        List<Stream> streams = streamRouter.route(msg);
        msg.addStreams(streams);

        LOG.debug("Routed message <{}> to {} streams.", msg.getId(), streams.size());

        return false;
    }

    @Override
    public String getName() {
        return "StreamMatcher";
    }

    @Override
    public int getPriority() {
        // of the built-in filters this gets run last
        return 40;
    }

}
