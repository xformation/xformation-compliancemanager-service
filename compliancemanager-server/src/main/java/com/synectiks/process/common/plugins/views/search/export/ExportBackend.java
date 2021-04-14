/*
 * */
package com.synectiks.process.common.plugins.views.search.export;

import java.util.function.Consumer;

public interface ExportBackend {
    void run(ExportMessagesCommand request, Consumer<SimpleMessageChunk> chunkCollector);
}
