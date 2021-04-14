/*
 * */
package com.synectiks.process.server.plugin;

import java.util.Collections;
import java.util.Iterator;

public class EmptyMessages implements Messages {
    private static final EmptyMessages EMPTY_MESSAGES = new EmptyMessages();

    @Override
    public Iterator<Message> iterator() {
        return Collections.emptyIterator();
    }
}
