/*
 * */
package com.synectiks.process.server.plugin.buffers;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.synectiks.process.server.plugin.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lennart Koopmann <lennart@socketfeed.com>
 */
public abstract class Buffer {
    private static final Logger log = LoggerFactory.getLogger(Buffer.class);

    protected RingBuffer<MessageEvent> ringBuffer;
    protected int ringBufferSize;

    public boolean isEmpty() {
        return getUsage() == 0;
    }

    public long getRemainingCapacity() {
        return ringBuffer.remainingCapacity();
    }

    public int getRingBufferSize() {
        return ringBufferSize;
    }

    public long getUsage() {
        if (ringBuffer == null) {
            return 0;
        }
        return (long) ringBuffer.getBufferSize() - ringBuffer.remainingCapacity();
    }

    protected void insert(Message message) {
        long sequence = ringBuffer.next();
        MessageEvent event = ringBuffer.get(sequence);
        event.setMessage(message);
        ringBuffer.publish(sequence);

        afterInsert(1);

    }

    protected WaitStrategy getWaitStrategy(String waitStrategyName, String configOptionName) {
        switch (waitStrategyName) {
            case "sleeping":
                return new SleepingWaitStrategy();
            case "yielding":
                return new YieldingWaitStrategy();
            case "blocking":
                return new BlockingWaitStrategy();
            case "busy_spinning":
                return new BusySpinWaitStrategy();
            default:
                log.warn("Invalid setting for [{}]:"
                                + " Falling back to default: BlockingWaitStrategy.", configOptionName);
                return new BlockingWaitStrategy();
        }
    }

    protected abstract void afterInsert(int n);

    protected void insert(Message[] messages) {
        int length = messages.length;
        long hi = ringBuffer.next(length);
        long lo = hi - (length - 1);
        for (long sequence = lo; sequence <= hi; sequence++) {
            MessageEvent event = ringBuffer.get(sequence);
            event.setMessage(messages[(int)(sequence - lo)]);
        }
        ringBuffer.publish(lo, hi);
        afterInsert(length);
    }
}
