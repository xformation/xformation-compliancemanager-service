/*
 * */
package com.synectiks.process.common.plugins.netflow.transport;

import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.synectiks.process.common.plugins.netflow.codecs.NetflowV9CodecAggregator;
import com.synectiks.process.common.plugins.netflow.transport.NetFlowUdpTransport;
import com.synectiks.process.common.plugins.netflow.transport.NetflowMessageAggregationHandler;
import com.synectiks.process.server.inputs.transports.NettyTransportConfiguration;
import com.synectiks.process.server.inputs.transports.netty.EventLoopGroupFactory;
import com.synectiks.process.server.plugin.LocalMetricRegistry;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.plugin.inputs.util.ThroughputCounter;

import java.util.LinkedHashMap;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class NetFlowUdpTransportTest {
    private EventLoopGroup eventLoopGroup;
    private EventLoopGroupFactory eventLoopGroupFactory;
    private NetFlowUdpTransport transport;

    @Before
    public void setUp() {
        final NettyTransportConfiguration nettyTransportConfiguration = new NettyTransportConfiguration("nio", "jdk", 1);
        eventLoopGroupFactory = new EventLoopGroupFactory(nettyTransportConfiguration);
        eventLoopGroup = new NioEventLoopGroup(1);
        transport = new NetFlowUdpTransport(
                Configuration.EMPTY_CONFIGURATION,
                eventLoopGroupFactory,
                nettyTransportConfiguration,
                new ThroughputCounter(eventLoopGroup),
                new LocalMetricRegistry());
        transport.setMessageAggregator(new NetflowV9CodecAggregator());
    }

    @After
    public void tearDown() {
        eventLoopGroup.shutdownGracefully();
    }

    @Test
    public void getChildChannelHandlersContainsCustomCodecAggregator() throws Exception {
        final LinkedHashMap<String, Callable<? extends ChannelHandler>> handlers = transport.getChannelHandlers(mock(MessageInput.class));
        assertThat(handlers)
                .containsKey("codec-aggregator")
                .doesNotContainKey("udp-datagram");

        final ChannelHandler channelHandler = handlers.get("codec-aggregator").call();
        assertThat(channelHandler).isInstanceOf(NetflowMessageAggregationHandler.class);
    }
}