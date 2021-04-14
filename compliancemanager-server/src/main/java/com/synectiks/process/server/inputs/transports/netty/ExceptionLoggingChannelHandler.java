/*
 * */
package com.synectiks.process.server.inputs.transports.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;

import org.slf4j.Logger;

import com.synectiks.process.server.plugin.inputs.MessageInput;

public class ExceptionLoggingChannelHandler extends ChannelInboundHandlerAdapter {
    private final MessageInput input;
    private final Logger logger;
    private final boolean keepAliveEnabled;

    public ExceptionLoggingChannelHandler(MessageInput input, Logger logger) {
        this(input, logger, false);
    }

    public ExceptionLoggingChannelHandler(MessageInput input, Logger logger, boolean keepAliveEnabled) {
        this.input = input;
        this.logger = logger;
        this.keepAliveEnabled = keepAliveEnabled;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (logger.isTraceEnabled() && "Connection reset by peer".equals(cause.getMessage())) {
            logger.trace("{} in Input [{}/{}] (channel {})",
                    cause.getMessage(),
                    input.getName(),
                    input.getId(),
                    ctx.channel());
        } else if (this.keepAliveEnabled && cause instanceof ReadTimeoutException) {
            if (logger.isTraceEnabled()) {
                logger.trace("KeepAlive Timeout in input [{}/{}] (channel {})",
                        input.getName(),
                        input.getId(),
                        ctx.channel());
            }
        } else {
            logger.error("Error in Input [{}/{}] (channel {}) (cause {})",
                    input.getName(),
                    input.getId(),
                    ctx.channel(),
                    cause);
        }

        ctx.close();
    }
}
