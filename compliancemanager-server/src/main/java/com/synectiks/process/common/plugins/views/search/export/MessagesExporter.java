/*
 * */
package com.synectiks.process.common.plugins.views.search.export;

import java.util.function.Consumer;

public interface MessagesExporter {
    void export(ExportMessagesCommand command, Consumer<SimpleMessageChunk> chunkForwarder);
}
