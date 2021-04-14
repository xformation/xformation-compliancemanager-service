/*
 * */
package com.synectiks.process.server.inputs.transports;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.synectiks.process.server.inputs.transports.netty.EventLoopGroupFactory;
import com.synectiks.process.server.inputs.transports.netty.LenientDelimiterBasedFrameDecoder;
import com.synectiks.process.server.plugin.LocalMetricRegistry;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.configuration.fields.BooleanField;
import com.synectiks.process.server.plugin.configuration.fields.ConfigurationField;
import com.synectiks.process.server.plugin.configuration.fields.NumberField;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.plugin.inputs.annotations.ConfigClass;
import com.synectiks.process.server.plugin.inputs.annotations.FactoryClass;
import com.synectiks.process.server.plugin.inputs.transports.AbstractTcpTransport;
import com.synectiks.process.server.plugin.inputs.transports.Transport;
import com.synectiks.process.server.plugin.inputs.util.ThroughputCounter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;

import java.util.LinkedHashMap;
import java.util.concurrent.Callable;

import static io.netty.handler.codec.Delimiters.lineDelimiter;
import static io.netty.handler.codec.Delimiters.nulDelimiter;

public class TcpTransport extends AbstractTcpTransport {
    public static final String CK_USE_NULL_DELIMITER = "use_null_delimiter";
    private static final String CK_MAX_MESSAGE_SIZE = "max_message_size";
    private static final int DEFAULT_MAX_FRAME_LENGTH = 2 * 1024 * 1024;

    protected final ByteBuf[] delimiter;
    protected final int maxFrameLength;

    @AssistedInject
    public TcpTransport(@Assisted Configuration configuration,
                        EventLoopGroup eventLoopGroup,
                        EventLoopGroupFactory eventLoopGroupFactory,
                        NettyTransportConfiguration nettyTransportConfiguration,
                        ThroughputCounter throughputCounter,
                        LocalMetricRegistry localRegistry,
                        com.synectiks.process.server.Configuration serverConfiguration) {
        super(configuration, throughputCounter, localRegistry, eventLoopGroup, eventLoopGroupFactory, nettyTransportConfiguration, serverConfiguration);

        final boolean nulDelimiter = configuration.getBoolean(CK_USE_NULL_DELIMITER);
        this.delimiter = nulDelimiter ? nulDelimiter() : lineDelimiter();
        this.maxFrameLength = configuration.getInt(CK_MAX_MESSAGE_SIZE, DEFAULT_MAX_FRAME_LENGTH);
    }

    @Override
    protected LinkedHashMap<String, Callable<? extends ChannelHandler>> getCustomChildChannelHandlers(MessageInput input) {
        final LinkedHashMap<String, Callable<? extends ChannelHandler>> childChannelHandlers = new LinkedHashMap<>();

        childChannelHandlers.put("framer", () -> new LenientDelimiterBasedFrameDecoder(maxFrameLength, delimiter));
        childChannelHandlers.putAll(super.getCustomChildChannelHandlers(input));

        return childChannelHandlers;
    }


    @FactoryClass
    public interface Factory extends Transport.Factory<TcpTransport> {
        @Override
        TcpTransport create(Configuration configuration);

        @Override
        Config getConfig();
    }

    @ConfigClass
    public static class Config extends AbstractTcpTransport.Config {
        @Override
        public ConfigurationRequest getRequestedConfiguration() {
            final ConfigurationRequest x = super.getRequestedConfiguration();

            x.addField(
                    new BooleanField(
                            CK_USE_NULL_DELIMITER,
                            "Null frame delimiter?",
                            false,
                            "Use null byte as frame delimiter? Otherwise newline delimiter is used."
                    )
            );
            x.addField(
                    new NumberField(
                            CK_MAX_MESSAGE_SIZE,
                            "Maximum message size",
                            DEFAULT_MAX_FRAME_LENGTH,
                            "The maximum length of a message.",
                            ConfigurationField.Optional.OPTIONAL,
                            NumberField.Attribute.ONLY_POSITIVE
                    )
            );

            return x;
        }
    }
}
