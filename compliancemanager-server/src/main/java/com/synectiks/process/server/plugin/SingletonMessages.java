/*
 * */
package com.synectiks.process.server.plugin;

import com.google.common.collect.Iterators;

import java.util.Iterator;

public class SingletonMessages implements Messages {

    private final Message message;

    public SingletonMessages(Message message) {
        this.message = message;
    }

    @Override
    public Iterator<Message> iterator() {
        return Iterators.singletonIterator(message);
    }

}
