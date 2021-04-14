/*
 * */
package com.synectiks.process.common.plugins.views.search.export;

import java.util.function.Consumer;

public class ChunkForwarder<T> {
    private final Consumer<T> onChunk;
    private final Runnable onClosed;

    public static <T> ChunkForwarder<T> create(Consumer<T> onChunk, Runnable onClosed) {
        return new ChunkForwarder<>(onChunk, onClosed);
    }

    public ChunkForwarder(Consumer<T> onChunk, Runnable onDone) {
        this.onChunk = onChunk;
        this.onClosed = onDone;
    }

    public void write(T chunk) {
        onChunk.accept(chunk);
    }

    public void close() {
        onClosed.run();
    }
}
