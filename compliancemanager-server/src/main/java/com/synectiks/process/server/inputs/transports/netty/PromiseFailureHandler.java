/*
 * */
package com.synectiks.process.server.inputs.transports.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
@ChannelHandler.Sharable
public class PromiseFailureHandler extends ChannelOutboundHandlerAdapter {
    public static final PromiseFailureHandler INSTANCE = new PromiseFailureHandler();
    private static final Logger LOG = LoggerFactory.getLogger(PromiseFailureHandler.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        promise.addListener(Listener.INSTANCE);
        super.write(ctx, msg, promise);
    }


    private static final class Listener implements ChannelFutureListener {
        private static final Listener INSTANCE = new Listener();

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (!future.isSuccess()) {
                LOG.info("Write on channel {} failed", future.channel(), future.cause());
            }
        }
    }
}

