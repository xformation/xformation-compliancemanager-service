/*
 * */
package com.synectiks.process.server.shared.events;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.synectiks.process.server.shared.events.DeadEventLoggingListener;

import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DeadEventLoggingListenerTest {

    @Test
    public void testHandleDeadEvent() {
        final DeadEventLoggingListener listener = new DeadEventLoggingListener();
        final DeadEvent event = new DeadEvent(this, new SimpleEvent("test"));

        listener.handleDeadEvent(event);
    }

    @Test
    public void testEventListenerWithEventBus() {
        final EventBus eventBus = new EventBus("test");
        final SimpleEvent event = new SimpleEvent("test");
        final DeadEventLoggingListener listener = spy(new DeadEventLoggingListener());
        eventBus.register(listener);

        eventBus.post(event);

        verify(listener, times(1)).handleDeadEvent(any(DeadEvent.class));
    }

    public static class SimpleEvent {
        public String payload;

        public SimpleEvent(String payload) {
            this.payload = payload;
        }

        @Override
        public String toString() {
            return "payload=" + payload;
        }
    }
}