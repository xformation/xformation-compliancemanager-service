/*
 * */
package com.synectiks.process.server.inputs.transports.netty;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.synectiks.process.server.plugin.inputs.codecs.CodecAggregator;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class EnvelopeMessageAggregationHandler extends SimpleChannelInboundHandler<AddressedEnvelope<ByteBuf, InetSocketAddress>> {
    private static final Logger LOG = LoggerFactory.getLogger(EnvelopeMessageAggregationHandler.class);

    private final CodecAggregator aggregator;
    private final Timer aggregationTimer;
    private final Meter invalidChunksMeter;

    public EnvelopeMessageAggregationHandler(CodecAggregator aggregator, MetricRegistry metricRegistry) {
        this.aggregator = aggregator;
        aggregationTimer = metricRegistry.timer("aggregationTime");
        invalidChunksMeter = metricRegistry.meter("invalidMessages");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AddressedEnvelope<ByteBuf, InetSocketAddress> envelope) throws Exception {
        final CodecAggregator.Result result;
        try (Timer.Context ignored = aggregationTimer.time()) {
            result = aggregator.addChunk(envelope.content());
        }
        final ByteBuf completeMessage = result.getMessage();
        if (completeMessage != null) {
            LOG.debug("Message aggregation completion, forwarding {}", completeMessage);
            ctx.fireChannelRead(SenderEnvelope.of(completeMessage, envelope.sender()));
        } else if (result.isValid()) {
            LOG.debug("More chunks necessary to complete this message");
        } else {
            invalidChunksMeter.mark();
            LOG.debug("Message chunk was not valid and discarded.");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        LOG.error("Caught exception while decoding type of GELF packet: {}", e.getMessage());
        invalidChunksMeter.mark();
    }
}
