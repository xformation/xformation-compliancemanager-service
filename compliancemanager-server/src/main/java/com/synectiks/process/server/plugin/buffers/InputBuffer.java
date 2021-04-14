/*
 * */
package com.synectiks.process.server.plugin.buffers;

import com.synectiks.process.server.plugin.journal.RawMessage;

public interface InputBuffer {
    void insert(RawMessage message);

    long getUsage();
}
