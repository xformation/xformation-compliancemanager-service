/*
 * */
package com.synectiks.process.common.plugins.views.search.export;

import javax.inject.Inject;
import java.util.function.Consumer;

public class DecoratingMessagesExporter implements MessagesExporter {
    private final ExportBackend backend;
    private final ChunkDecorator chunkDecorator;

    @Inject
    public DecoratingMessagesExporter(
            ExportBackend backend,
            ChunkDecorator chunkDecorator) {
        this.backend = backend;
        this.chunkDecorator = chunkDecorator;
    }

    public void export(ExportMessagesCommand command, Consumer<SimpleMessageChunk> chunkForwarder) {
        Consumer<SimpleMessageChunk> decoratedForwarder = chunk -> decorate(chunkForwarder, chunk, command);

        backend.run(command, decoratedForwarder);
    }

    private void decorate(Consumer<SimpleMessageChunk> chunkForwarder, SimpleMessageChunk chunk, ExportMessagesCommand command) {
        SimpleMessageChunk decoratedChunk = chunkDecorator.decorate(chunk, command);

        chunkForwarder.accept(decoratedChunk);
    }
}
