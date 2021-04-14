/*
 * */
package com.synectiks.process.server.plugin.inputs.codecs;

import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.journal.RawMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public interface MultiMessageCodec extends Codec {
    @Nullable
    Collection<Message> decodeMessages(@Nonnull RawMessage rawMessage);
}
