/*
 * */
package com.synectiks.process.server.inputs.transports;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.synectiks.process.server.inputs.syslog.tcp.SyslogTCPFramingRouterHandler;
import com.synectiks.process.server.inputs.transports.netty.EventLoopGroupFactory;
import com.synectiks.process.server.plugin.LocalMetricRegistry;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.plugin.inputs.annotations.ConfigClass;
import com.synectiks.process.server.plugin.inputs.annotations.FactoryClass;
import com.synectiks.process.server.plugin.inputs.transports.Transport;
import com.synectiks.process.server.plugin.inputs.util.ThroughputCounter;

import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;

import java.util.LinkedHashMap;
import java.util.concurrent.Callable;

public class SyslogTcpTransport extends TcpTransport {
    @AssistedInject
    public SyslogTcpTransport(@Assisted Configuration configuration,
                              EventLoopGroup eventLoopGroup,
                              EventLoopGroupFactory eventLoopGroupFactory,
                              NettyTransportConfiguration nettyTransportConfiguration,
                              ThroughputCounter throughputCounter,
                              LocalMetricRegistry localRegistry,
                              com.synectiks.process.server.Configuration serverConfiguration) {
        super(configuration,
                eventLoopGroup,
                eventLoopGroupFactory,
                nettyTransportConfiguration,
                throughputCounter,
                localRegistry,
                serverConfiguration);
    }

    @Override
    protected LinkedHashMap<String, Callable<? extends ChannelHandler>> getCustomChildChannelHandlers(MessageInput input) {
        final LinkedHashMap<String, Callable<? extends ChannelHandler>> finalChannelHandlers = new LinkedHashMap<>(super.getCustomChildChannelHandlers(input));

        // Replace the "framer" channel handler inserted by the parent.
        finalChannelHandlers.replace("framer", () -> new SyslogTCPFramingRouterHandler(maxFrameLength, delimiter));

        return finalChannelHandlers;
    }

    @FactoryClass
    public interface Factory extends Transport.Factory<SyslogTcpTransport> {
        @Override
        SyslogTcpTransport create(Configuration configuration);
    }

    @ConfigClass
    public static class Config extends TcpTransport.Config {
    }
}
