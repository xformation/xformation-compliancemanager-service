/*
 * */
package com.synectiks.process.server.plugin.messageprocessors;

import com.synectiks.process.server.plugin.Messages;

public interface MessageProcessor {
    interface Descriptor {
        String name();
        String className();
    }

    Messages process(Messages messages);
}
