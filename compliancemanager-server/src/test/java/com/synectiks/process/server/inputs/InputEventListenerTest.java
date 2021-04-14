/*
 * */
package com.synectiks.process.server.inputs;

import com.google.common.eventbus.EventBus;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.inputs.Input;
import com.synectiks.process.server.inputs.InputEventListener;
import com.synectiks.process.server.inputs.InputService;
import com.synectiks.process.server.plugin.IOState;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.plugin.system.NodeId;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputCreated;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputDeleted;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputUpdated;
import com.synectiks.process.server.shared.inputs.InputLauncher;
import com.synectiks.process.server.shared.inputs.InputRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class InputEventListenerTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private InputLauncher inputLauncher;
    @Mock
    private InputRegistry inputRegistry;
    @Mock
    private InputService inputService;
    @Mock
    private NodeId nodeId;
    private InputEventListener listener;

    @Before
    public void setUp() throws Exception {
        final EventBus eventBus = new EventBus(this.getClass().getSimpleName());
        listener = new InputEventListener(eventBus, inputLauncher, inputRegistry, inputService, nodeId);
    }

    @Test
    public void inputCreatedDoesNothingIfInputDoesNotExist() throws Exception {
        final String inputId = "input-id";
        when(inputService.find(inputId)).thenThrow(NotFoundException.class);

        listener.inputCreated(InputCreated.create(inputId));

        verifyZeroInteractions(inputLauncher, inputRegistry, nodeId);
    }

    @Test
    public void inputCreatedStopsInputIfItIsRunning() throws Exception {
        final String inputId = "input-id";
        final Input input = mock(Input.class);
        @SuppressWarnings("unchecked") final IOState<MessageInput> inputState = mock(IOState.class);
        when(inputService.find(inputId)).thenReturn(input);
        when(inputRegistry.getInputState(inputId)).thenReturn(inputState);

        listener.inputCreated(InputCreated.create(inputId));

        verify(inputRegistry, times(1)).remove(inputState);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void inputCreatedDoesNotStopInputIfItIsNotRunning() throws Exception {
        final String inputId = "input-id";
        final Input input = mock(Input.class);
        when(inputService.find(inputId)).thenReturn(input);
        when(inputRegistry.getInputState(inputId)).thenReturn(null);

        listener.inputCreated(InputCreated.create(inputId));

        verify(inputRegistry, never()).remove(any(IOState.class));
    }

    @Test
    public void inputCreatedStartsGlobalInputOnOtherNode() throws Exception {
        final String inputId = "input-id";
        final Input input = mock(Input.class);
        when(inputService.find(inputId)).thenReturn(input);
        when(nodeId.toString()).thenReturn("node-id");
        when(input.getNodeId()).thenReturn("other-node-id");
        when(input.isGlobal()).thenReturn(true);

        final MessageInput messageInput = mock(MessageInput.class);
        when(inputService.getMessageInput(input)).thenReturn(messageInput);

        listener.inputCreated(InputCreated.create(inputId));

        verify(inputLauncher, times(1)).launch(messageInput);
    }

    @Test
    public void inputCreatedDoesNotStartLocalInputOnAnyNode() throws Exception {
        final String inputId = "input-id";
        final Input input = mock(Input.class);
        when(inputService.find(inputId)).thenReturn(input);
        when(nodeId.toString()).thenReturn("node-id");
        when(input.getNodeId()).thenReturn("other-node-id");
        when(input.isGlobal()).thenReturn(false);

        final MessageInput messageInput = mock(MessageInput.class);
        when(inputService.getMessageInput(input)).thenReturn(messageInput);

        listener.inputCreated(InputCreated.create(inputId));

        verify(inputLauncher, never()).launch(messageInput);
    }

    @Test
    public void inputCreatedStartsLocalInputOnLocalNode() throws Exception {
        final String inputId = "input-id";
        final Input input = mock(Input.class);
        when(inputService.find(inputId)).thenReturn(input);
        when(nodeId.toString()).thenReturn("node-id");
        when(input.getNodeId()).thenReturn("node-id");
        when(input.isGlobal()).thenReturn(false);

        final MessageInput messageInput = mock(MessageInput.class);
        when(inputService.getMessageInput(input)).thenReturn(messageInput);

        listener.inputCreated(InputCreated.create(inputId));

        verify(inputLauncher, times(1)).launch(messageInput);
    }

    @Test
    public void inputUpdatedDoesNothingIfInputDoesNotExist() throws Exception {
        final String inputId = "input-id";
        when(inputService.find(inputId)).thenThrow(NotFoundException.class);

        listener.inputUpdated(InputUpdated.create(inputId));

        verifyZeroInteractions(inputLauncher, inputRegistry, nodeId);
    }

    @Test
    public void inputUpdatedStopsInputIfItIsRunning() throws Exception {
        final String inputId = "input-id";
        final Input input = mock(Input.class);
        @SuppressWarnings("unchecked") final IOState<MessageInput> inputState = mock(IOState.class);
        when(inputService.find(inputId)).thenReturn(input);
        when(inputRegistry.getInputState(inputId)).thenReturn(inputState);

        listener.inputUpdated(InputUpdated.create(inputId));

        verify(inputRegistry, times(1)).remove(inputState);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void inputUpdatedDoesNotStopInputIfItIsNotRunning() throws Exception {
        final String inputId = "input-id";
        final Input input = mock(Input.class);
        when(inputService.find(inputId)).thenReturn(input);
        when(inputRegistry.getInputState(inputId)).thenReturn(null);

        listener.inputUpdated(InputUpdated.create(inputId));

        verify(inputRegistry, never()).remove(any(IOState.class));
    }

    @Test
    public void inputUpdatedRestartsGlobalInputOnAnyNode() throws Exception {
        final String inputId = "input-id";
        final Input input = mock(Input.class);
        @SuppressWarnings("unchecked") final IOState<MessageInput> inputState = mock(IOState.class);
        when(inputState.getState()).thenReturn(IOState.Type.RUNNING);
        when(inputService.find(inputId)).thenReturn(input);
        when(inputRegistry.getInputState(inputId)).thenReturn(inputState);
        when(nodeId.toString()).thenReturn("node-id");
        when(input.getNodeId()).thenReturn("other-node-id");
        when(input.isGlobal()).thenReturn(true);

        final MessageInput messageInput = mock(MessageInput.class);
        when(inputService.getMessageInput(input)).thenReturn(messageInput);

        listener.inputUpdated(InputUpdated.create(inputId));

        verify(inputLauncher, times(1)).launch(messageInput);
    }

    @Test
    public void inputUpdatedDoesNotStartLocalInputOnOtherNode() throws Exception {
        final String inputId = "input-id";
        final Input input = mock(Input.class);
        @SuppressWarnings("unchecked") final IOState<MessageInput> inputState = mock(IOState.class);
        when(inputState.getState()).thenReturn(IOState.Type.RUNNING);
        when(inputService.find(inputId)).thenReturn(input);
        when(nodeId.toString()).thenReturn("node-id");
        when(input.getNodeId()).thenReturn("other-node-id");
        when(input.isGlobal()).thenReturn(false);

        final MessageInput messageInput = mock(MessageInput.class);
        when(inputService.getMessageInput(input)).thenReturn(messageInput);

        listener.inputUpdated(InputUpdated.create(inputId));

        verify(inputLauncher, never()).launch(messageInput);
    }

    @Test
    public void inputUpdatedRestartsLocalInputOnLocalNode() throws Exception {
        final String inputId = "input-id";
        final Input input = mock(Input.class);
        @SuppressWarnings("unchecked") final IOState<MessageInput> inputState = mock(IOState.class);
        when(inputState.getState()).thenReturn(IOState.Type.RUNNING);
        when(inputService.find(inputId)).thenReturn(input);
        when(inputRegistry.getInputState(inputId)).thenReturn(inputState);
        when(nodeId.toString()).thenReturn("node-id");
        when(input.getNodeId()).thenReturn("node-id");
        when(input.isGlobal()).thenReturn(false);

        final MessageInput messageInput = mock(MessageInput.class);
        when(inputService.getMessageInput(input)).thenReturn(messageInput);

        listener.inputUpdated(InputUpdated.create(inputId));

        verify(inputLauncher, times(1)).launch(messageInput);
    }

    @Test
    public void inputUpdatedDoesNotStartLocalInputOnLocalNodeIfItWasNotRunning() throws Exception {
        final String inputId = "input-id";
        final Input input = mock(Input.class);
        @SuppressWarnings("unchecked") final IOState<MessageInput> inputState = mock(IOState.class);
        when(inputState.getState()).thenReturn(IOState.Type.STOPPED);
        when(inputService.find(inputId)).thenReturn(input);
        when(inputRegistry.getInputState(inputId)).thenReturn(inputState);
        when(nodeId.toString()).thenReturn("node-id");
        when(input.getNodeId()).thenReturn("node-id");
        when(input.isGlobal()).thenReturn(false);

        final MessageInput messageInput = mock(MessageInput.class);
        when(inputService.getMessageInput(input)).thenReturn(messageInput);

        listener.inputUpdated(InputUpdated.create(inputId));

        verify(inputLauncher, never()).launch(messageInput);
    }

    @Test
    public void inputDeletedStopsInputIfItIsRunning() throws Exception {
        final String inputId = "input-id";
        @SuppressWarnings("unchecked") final IOState<MessageInput> inputState = mock(IOState.class);
        when(inputState.getState()).thenReturn(IOState.Type.RUNNING);
        when(inputRegistry.getInputState(inputId)).thenReturn(inputState);

        listener.inputDeleted(InputDeleted.create(inputId));

        verify(inputRegistry, never()).remove(any(MessageInput.class));
    }

    @Test
    public void inputDeletedDoesNothingIfInputIsNotRunning() throws Exception {
        final String inputId = "input-id";
        @SuppressWarnings("unchecked") final IOState<MessageInput> inputState = mock(IOState.class);
        when(inputState.getState()).thenReturn(null);
        when(inputRegistry.getInputState(inputId)).thenReturn(inputState);

        listener.inputDeleted(InputDeleted.create(inputId));

        verify(inputRegistry, never()).remove(any(MessageInput.class));
    }
}