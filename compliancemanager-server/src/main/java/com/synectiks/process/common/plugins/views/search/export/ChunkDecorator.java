/*
 * */
package com.synectiks.process.common.plugins.views.search.export;

public interface ChunkDecorator {
    SimpleMessageChunk decorate(SimpleMessageChunk chunk, ExportMessagesCommand request);
}
