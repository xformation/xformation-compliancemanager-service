/*
 * */
package com.synectiks.process.common.plugins.netflow.transport;

import com.google.inject.assistedinject.Assisted;
import com.synectiks.process.common.plugins.netflow.codecs.RemoteAddressCodecAggregator;
import com.synectiks.process.server.inputs.transports.NettyTransportConfiguration;
import com.synectiks.process.server.inputs.transports.UdpTransport;
import com.synectiks.process.server.inputs.transports.netty.EventLoopGroupFactory;
import com.synectiks.process.server.plugin.LocalMetricRegistry;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.plugin.inputs.annotations.ConfigClass;
import com.synectiks.process.server.plugin.inputs.annotations.FactoryClass;
import com.synectiks.process.server.plugin.inputs.transports.Transport;
import com.synectiks.process.server.plugin.inputs.util.ThroughputCounter;

import io.netty.channel.ChannelHandler;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.concurrent.Callable;

/**
 * This UDP transport is largely identical to its superclass, but replaces the codec aggregator and its handler with custom
 * implementations that are able to pass the remote address.
 *
 * Without the remote address the NetFlow V9 code cannot distinguish between flows from different exporters and thus might
 * handle template flows incorrectly should they differ between exporters.
 *
 * @see <a href="https://tools.ietf.org/html/rfc3954#section-5.1">RFC 3953 - Source ID</a>
 */
public class NetFlowUdpTransport extends UdpTransport {
    @Inject
    public NetFlowUdpTransport(@Assisted Configuration configuration,
                               EventLoopGroupFactory eventLoopGroupFactory,
                               NettyTransportConfiguration nettyTransportConfiguration,
                               ThroughputCounter throughputCounter,
                               LocalMetricRegistry localRegistry) {
        super(configuration, eventLoopGroupFactory, nettyTransportConfiguration, throughputCounter, localRegistry);
    }

    @Override
    protected LinkedHashMap<String, Callable<? extends ChannelHandler>> getChannelHandlers(MessageInput input) {
        final LinkedHashMap<String, Callable<? extends ChannelHandler>> handlers = new LinkedHashMap<>(super.getChannelHandlers(input));

        // Replace the default "codec-aggregator" handler with one that passes the remote address
        final RemoteAddressCodecAggregator aggregator = (RemoteAddressCodecAggregator) getAggregator();
        handlers.replace("codec-aggregator", () -> new NetflowMessageAggregationHandler(aggregator, localRegistry));
        handlers.remove("udp-datagram");

        return handlers;
    }

    @FactoryClass
    public interface Factory extends Transport.Factory<NetFlowUdpTransport> {
        @Override
        NetFlowUdpTransport create(Configuration configuration);

        @Override
        NetFlowUdpTransport.Config getConfig();
    }

    @ConfigClass
    public static class Config extends UdpTransport.Config {
    }
}
