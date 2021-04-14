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

import static com.google.common.collect.ImmutableList.of;
import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.bool;
import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.string;
import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.type;

public class RouteToStream extends AbstractFunction<Void> {

    public static final String NAME = "route_to_stream";
    private static final String ID_ARG = "id";
    private static final String NAME_ARG = "name";
    private static final String REMOVE_FROM_DEFAULT = "remove_from_default";
    private final StreamCacheService streamCacheService;
    private final Provider<Stream> defaultStreamProvider;
    private final ParameterDescriptor<Message, Message> messageParam;
    private final ParameterDescriptor<String, String> nameParam;
    private final ParameterDescriptor<String, String> idParam;
    private final ParameterDescriptor<Boolean, Boolean> removeFromDefault;

    @Inject
    public RouteToStream(StreamCacheService streamCacheService, @DefaultStream Provider<Stream> defaultStreamProvider) {
        this.streamCacheService = streamCacheService;
        this.defaultStreamProvider = defaultStreamProvider;

        messageParam = type("message", Message.class).optional().description("The message to use, defaults to '$message'").build();
        nameParam = string(NAME_ARG).optional().description("The name of the stream to route the message to, must match exactly").build();
        idParam = string(ID_ARG).optional().description("The ID of the stream").build();
        removeFromDefault = bool(REMOVE_FROM_DEFAULT).optional().description("After routing the message, remove it from the default stream").build();
    }

    @Override
    public Void evaluate(FunctionArgs args, EvaluationContext context) {
        String id = idParam.optional(args, context).orElse("");

        final Collection<Stream> streams;
        if ("".equals(id)) {
            final String name = nameParam.optional(args, context).orElse("");
            if ("".equals(name)) {
                return null;
            }
            streams = streamCacheService.getByName(name);
            if (streams.isEmpty()) {
                // TODO signal error somehow
                return null;
            }
        } else {
            final Stream stream = streamCacheService.getById(id);
            if (stream == null) {
                return null;
            }
            streams = Collections.singleton(stream);
        }
        final Message message = messageParam.optional(args, context).orElse(context.currentMessage());
        streams.forEach(stream -> {
            if (!stream.isPaused()) {
                message.addStream(stream);
            }
        });
        if (removeFromDefault.optional(args, context).orElse(Boolean.FALSE)) {
            message.removeStream(defaultStreamProvider.get());
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
                        messageParam,
                        removeFromDefault))
                .description("Routes a message to a stream")
                .build();
    }
}
