/*
 * */
package com.synectiks.process.server.inputs.transports;

import com.github.joschi.jadconfig.util.Size;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.synectiks.process.server.inputs.transports.netty.EventLoopGroupFactory;
import com.synectiks.process.server.inputs.transports.netty.HttpHandler;
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

import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.LinkedHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class HttpTransport extends AbstractTcpTransport {
    private static final int DEFAULT_MAX_INITIAL_LINE_LENGTH = 4096;
    private static final int DEFAULT_MAX_HEADER_SIZE = 8192;
    private static final int DEFAULT_MAX_CHUNK_SIZE = (int) Size.kilobytes(64L).toBytes();
    private static final int DEFAULT_IDLE_WRITER_TIMEOUT = 60;

    static final String CK_ENABLE_CORS = "enable_cors";
    static final String CK_MAX_CHUNK_SIZE = "max_chunk_size";
    static final String CK_IDLE_WRITER_TIMEOUT = "idle_writer_timeout";

    private final boolean enableCors;
    private final int maxChunkSize;
    private final int idleWriterTimeout;

    @AssistedInject
    public HttpTransport(@Assisted Configuration configuration,
                         EventLoopGroup eventLoopGroup,
                         EventLoopGroupFactory eventLoopGroupFactory,
                         NettyTransportConfiguration nettyTransportConfiguration,
                         ThroughputCounter throughputCounter,
                         LocalMetricRegistry localRegistry,
                         com.synectiks.process.server.Configuration serverConfiguration) {
        super(configuration,
              throughputCounter,
              localRegistry,
              eventLoopGroup,
              eventLoopGroupFactory,
              nettyTransportConfiguration,
              serverConfiguration);

        enableCors = configuration.getBoolean(CK_ENABLE_CORS);

        int maxChunkSize = configuration.intIsSet(CK_MAX_CHUNK_SIZE) ? configuration.getInt(CK_MAX_CHUNK_SIZE) : DEFAULT_MAX_CHUNK_SIZE;
        this.maxChunkSize = maxChunkSize <= 0 ? DEFAULT_MAX_CHUNK_SIZE : maxChunkSize;
        this.idleWriterTimeout = configuration.intIsSet(CK_IDLE_WRITER_TIMEOUT) ? configuration.getInt(CK_IDLE_WRITER_TIMEOUT, DEFAULT_IDLE_WRITER_TIMEOUT) : DEFAULT_IDLE_WRITER_TIMEOUT;
    }

    @Override
    protected LinkedHashMap<String, Callable<? extends ChannelHandler>> getCustomChildChannelHandlers(MessageInput input) {
        final LinkedHashMap<String, Callable<? extends ChannelHandler>> handlers = new LinkedHashMap<>();

        if (idleWriterTimeout > 0) {
            // Install read timeout handler to close idle connections after a timeout.
            // This avoids dangling HTTP connections when the HTTP client does not close the connection properly.
            handlers.put("read-timeout-handler", () -> new ReadTimeoutHandler(idleWriterTimeout, TimeUnit.SECONDS));
        }

        handlers.put("decoder", () -> new HttpRequestDecoder(DEFAULT_MAX_INITIAL_LINE_LENGTH, DEFAULT_MAX_HEADER_SIZE, maxChunkSize));
        handlers.put("decompressor", HttpContentDecompressor::new);
        handlers.put("encoder", HttpResponseEncoder::new);
        handlers.put("aggregator", () -> new HttpObjectAggregator(maxChunkSize));
        handlers.put("http-handler", () -> new HttpHandler(enableCors));
        handlers.putAll(super.getCustomChildChannelHandlers(input));

        return handlers;
    }

    @FactoryClass
    public interface Factory extends Transport.Factory<HttpTransport> {
        @Override
        HttpTransport create(Configuration configuration);

        @Override
        Config getConfig();
    }

    @ConfigClass
    public static class Config extends AbstractTcpTransport.Config {
        @Override
        public ConfigurationRequest getRequestedConfiguration() {
            final ConfigurationRequest r = super.getRequestedConfiguration();
            r.addField(new BooleanField(CK_ENABLE_CORS,
                                        "Enable CORS",
                                        true,
                                        "Input sends CORS headers to satisfy browser security policies"));
            r.addField(new NumberField(CK_MAX_CHUNK_SIZE,
                                        "Max. HTTP chunk size",
                                        DEFAULT_MAX_CHUNK_SIZE,
                                        "The maximum HTTP chunk size in bytes (e. g. length of HTTP request body)",
                                        ConfigurationField.Optional.OPTIONAL));
            r.addField(new NumberField(CK_IDLE_WRITER_TIMEOUT,
                                        "Idle writer timeout",
                                        DEFAULT_IDLE_WRITER_TIMEOUT,
                                        "The server closes the connection after the given time in seconds after the last client write request. (use 0 to disable)",
                                        ConfigurationField.Optional.OPTIONAL,
                                        NumberField.Attribute.ONLY_POSITIVE));
            return r;
        }
    }

}
