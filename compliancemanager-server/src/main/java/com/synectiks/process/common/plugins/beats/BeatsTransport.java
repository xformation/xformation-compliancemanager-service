/*
 * */
package com.synectiks.process.common.plugins.beats;

import com.google.inject.assistedinject.Assisted;
import com.synectiks.process.server.inputs.transports.NettyTransportConfiguration;
import com.synectiks.process.server.inputs.transports.netty.EventLoopGroupFactory;
import com.synectiks.process.server.plugin.LocalMetricRegistry;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.plugin.inputs.annotations.ConfigClass;
import com.synectiks.process.server.plugin.inputs.annotations.FactoryClass;
import com.synectiks.process.server.plugin.inputs.transports.AbstractTcpTransport;
import com.synectiks.process.server.plugin.inputs.transports.NettyTransport;
import com.synectiks.process.server.plugin.inputs.transports.Transport;
import com.synectiks.process.server.plugin.inputs.util.ThroughputCounter;

import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.concurrent.Callable;

public class BeatsTransport extends AbstractTcpTransport {
    @Inject
    public BeatsTransport(@Assisted Configuration configuration,
                          EventLoopGroup eventLoopGroup,
                          EventLoopGroupFactory eventLoopGroupFactory,
                          NettyTransportConfiguration nettyTransportConfiguration,
                          ThroughputCounter throughputCounter,
                          LocalMetricRegistry localRegistry,
                          com.synectiks.process.server.Configuration serverConfiguration) {
        super(configuration, throughputCounter, localRegistry, eventLoopGroup, eventLoopGroupFactory, nettyTransportConfiguration, serverConfiguration);
    }

    @Override
    protected LinkedHashMap<String, Callable<? extends ChannelHandler>> getCustomChildChannelHandlers(MessageInput input) {
        final LinkedHashMap<String, Callable<? extends ChannelHandler>> handlers = new LinkedHashMap<>(super.getCustomChildChannelHandlers(input));
        handlers.put("beats", BeatsFrameDecoder::new);

        return handlers;
    }

    @FactoryClass
    public interface Factory extends Transport.Factory<BeatsTransport> {
        @Override
        BeatsTransport create(Configuration configuration);

        @Override
        Config getConfig();
    }

    @ConfigClass
    public static class Config extends AbstractTcpTransport.Config {
        @Override
        public ConfigurationRequest getRequestedConfiguration() {
            final ConfigurationRequest cr = super.getRequestedConfiguration();
            if (cr.containsField(NettyTransport.CK_PORT)) {
                cr.getField(NettyTransport.CK_PORT).setDefaultValue(5044);
            }
            return cr;
        }
    }
}
