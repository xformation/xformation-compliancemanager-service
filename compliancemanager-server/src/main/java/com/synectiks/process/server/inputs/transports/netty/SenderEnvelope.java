/*
 * */
package com.synectiks.process.server.inputs.transports.netty;

import io.netty.channel.AddressedEnvelope;
import io.netty.channel.DefaultAddressedEnvelope;

import java.net.InetSocketAddress;

/**
 * Helper class to simplify envelope creation.
 */
public class SenderEnvelope {
    /**
     * Returns a {@link AddressedEnvelope} of the given message and message sender values.
     *
     * @param message the message
     * @param sender the sender address
     * @param <M> message type
     * @param <A> sender type
     * @return the envelope
     */
    public static <M, A extends InetSocketAddress> AddressedEnvelope<M, A> of(M message, A sender) {
        return new DefaultAddressedEnvelope<>(message, null, sender);
    }
}
