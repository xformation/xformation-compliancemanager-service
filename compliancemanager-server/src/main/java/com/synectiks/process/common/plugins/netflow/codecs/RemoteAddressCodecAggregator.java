/*
 * */
package com.synectiks.process.common.plugins.netflow.codecs;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.synectiks.process.server.plugin.inputs.codecs.CodecAggregator;

import java.net.SocketAddress;

public interface RemoteAddressCodecAggregator extends CodecAggregator {

    @Nonnull
    @Override
    default Result addChunk(ByteBuf buf) {
        return addChunk(buf, null);
    }

    @Nonnull
    Result addChunk(ByteBuf buf, @Nullable SocketAddress remoteAddress);
}
