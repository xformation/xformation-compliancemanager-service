/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.messages;

import com.google.inject.Inject;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.streams.DefaultStream;
import com.synectiks.process.server.plugin.streams.Stream;

import javax.inject.Provider;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static com.google.common.collect.ImmutableList.of;
import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.string;
import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.type;

public class RemoveFromStream extends AbstractFunction<Void> {

    public static final String NAME = "remove_from_stream";
    private static final String ID_ARG = "id";
    private static final String NAME_ARG = "name";
    private final StreamCacheService streamCacheService;
    private final Provider<Stream> defaultStreamProvider;
    private final ParameterDescriptor<Message, Message> messageParam;
    private final ParameterDescriptor<String, String> nameParam;
    private final ParameterDescriptor<String, String> idParam;

    @Inject
    public RemoveFromStream(StreamCacheService streamCacheService, @DefaultStream Provider<Stream> defaultStreamProvider) {
        this.streamCacheService = streamCacheService;
        this.defaultStreamProvider = defaultStreamProvider;

        messageParam = type("message", Message.class).optional().description("The message to use, defaults to '$message'").build();
        nameParam = string(NAME_ARG).optional().description("The name of the stream to remove the message from, must match exactly").build();
        idParam = string(ID_ARG).optional().description("The ID of the stream").build();
    }

    @Override
    public Void evaluate(FunctionArgs args, EvaluationContext context) {
        Optional<String> id = idParam.optional(args, context);

        Collection<Stream> streams;
        if (!id.isPresent()) {
            final Optional<Collection<Stream>> foundStreams = nameParam.optional(args, context).map(streamCacheService::getByName);

            if (!foundStreams.isPresent()) {
                // TODO signal error somehow
                return null;
            } else {
                streams = foundStreams.get();
            }
        } else {
            final Stream stream = streamCacheService.getById(id.get());
            if (stream == null) {
                return null;
            }
            streams = Collections.singleton(stream);
        }
        final Message message = messageParam.optional(args, context).orElse(context.currentMessage());
        streams.forEach(stream -> {
            if (!stream.isPaused()) {
                message.removeStream(stream);
            }
        });
        // always leave a message at least on the default stream if we removed the last stream it was on
        if (message.getStreams().isEmpty()) {
            message.addStream(defaultStreamProvider.get());
        }
        return null;
    }

    @Override
    public FunctionDescriptor<Void> descriptor() {
        return FunctionDescriptor.<Void>builder()
                .name(NAME)
                .returnType(Void.class)
                .params(of(
                        nameParam,
                        idParam,
                        messageParam))
                .description("Removes a message from a stream. Removing the last stream will put the message back onto the default stream. To complete drop a message use the drop_message function.")
                .build();
    }
}
