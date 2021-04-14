/*
 * */
package com.synectiks.process.server.plugin.inputs.codecs;

import com.google.common.base.MoreObjects;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface CodecAggregator {

    @Nonnull
    Result addChunk(ByteBuf buf);

    final class Result {
        private final ByteBuf message;
        private final boolean valid;

        public Result(@Nullable ByteBuf message, boolean valid) {
            this.message = message;
            this.valid = valid;
        }

        @Nullable
        public ByteBuf getMessage() {
            return message;
        }

        public boolean isValid() {
            return valid;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                .add("message", message)
                .add("valid", valid)
                .toString();
        }
    }
}
