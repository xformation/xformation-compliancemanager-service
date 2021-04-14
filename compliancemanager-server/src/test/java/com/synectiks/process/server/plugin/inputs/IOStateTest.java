/*
 * */
package com.synectiks.process.server.plugin.inputs;

import com.google.common.eventbus.EventBus;
import com.synectiks.process.server.plugin.IOState;
import com.synectiks.process.server.plugin.inputs.MessageInput;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class IOStateTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void testNotEqualIfDifferentInput() throws Exception {
        EventBus eventBus = mock(EventBus.class);
        MessageInput messageInput1 = mock(MessageInput.class);
        MessageInput messageInput2 = mock(MessageInput.class);

        IOState<MessageInput> inputState1 = new IOState<>(eventBus, messageInput1);
        IOState<MessageInput> inputState2 = new IOState<>(eventBus, messageInput2);

        assertFalse(inputState1.equals(inputState2));
        assertFalse(inputState2.equals(inputState1));
    }

    @Test
    public void testEqualsSameState() throws Exception {
        EventBus eventBus = mock(EventBus.class);
        MessageInput messageInput = mock(MessageInput.class);

        IOState<MessageInput> inputState1 = new IOState<>(eventBus, messageInput, IOState.Type.RUNNING);
        IOState<MessageInput> inputState2 = new IOState<>(eventBus, messageInput, IOState.Type.RUNNING);

        assertTrue(inputState1.equals(inputState2));
        assertTrue(inputState2.equals(inputState1));
    }

    @Test
    public void testNotEqualIfDifferentState() throws Exception {
        EventBus eventBus = mock(EventBus.class);
        MessageInput messageInput = mock(MessageInput.class);

        IOState<MessageInput> inputState1 = new IOState<>(eventBus, messageInput, IOState.Type.RUNNING);
        IOState<MessageInput> inputState2 = new IOState<>(eventBus, messageInput, IOState.Type.STOPPED);

        assertTrue(inputState1.equals(inputState2));
        assertTrue(inputState2.equals(inputState1));
    }
}
