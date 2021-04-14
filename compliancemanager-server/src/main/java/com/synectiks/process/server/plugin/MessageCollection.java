/*
 * */
package com.synectiks.process.server.plugin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;

import java.util.Iterator;

public class MessageCollection implements Messages  {

    private final ImmutableList<Message> messages;

    public MessageCollection(Iterable<Message> other) {
        messages = ImmutableList.copyOf(other);
    }

    @Override
    public Iterator<Message> iterator() {
        return Iterators.filter(messages.iterator(), e -> !e.getFilterOut());
    }
}
