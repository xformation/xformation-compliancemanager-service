/*
 * */
package com.synectiks.process.server.plugin.events.inputs;

import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.IOState;
import com.synectiks.process.server.plugin.Stoppable;

import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
public abstract class IOStateChangedEvent<T extends Stoppable> {
    public abstract IOState.Type oldState();
    public abstract IOState.Type newState();
    public abstract IOState<T> changedState();

    public static <K extends Stoppable> IOStateChangedEvent<K> create(IOState.Type oldState, IOState.Type newState, IOState<K> changedEvent) {
        return new AutoValue_IOStateChangedEvent<>(oldState, newState, changedEvent);
    }
}
