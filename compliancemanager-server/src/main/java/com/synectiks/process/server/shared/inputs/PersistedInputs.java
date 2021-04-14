/*
 * */
package com.synectiks.process.server.shared.inputs;

import com.synectiks.process.server.plugin.inputs.MessageInput;

public interface PersistedInputs extends Iterable<MessageInput> {
    MessageInput get(String id);
    boolean add(MessageInput e);
    boolean remove(Object o);
    boolean update(String id, MessageInput newInput);
}
