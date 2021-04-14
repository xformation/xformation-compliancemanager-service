/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.messages;

import org.joda.time.DateTime;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.Tools;

import java.util.Optional;

import static com.google.common.collect.ImmutableList.of;
import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.string;
import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.type;

public class CreateMessage extends AbstractFunction<Message> {

    public static final String NAME = "create_message";

    private static final String MESSAGE_ARG = "message";
    private static final String SOURCE_ARG = "source";
    private static final String TIMESTAMP_ARG = "timestamp";
    private final ParameterDescriptor<String, String> messageParam;
    private final ParameterDescriptor<String, String> sourceParam;
    private final ParameterDescriptor<DateTime, DateTime> timestampParam;

    public CreateMessage() {
        messageParam = string(MESSAGE_ARG).optional().description("The 'message' field of the new message, defaults to '$message.message'").build();
        sourceParam = string(SOURCE_ARG).optional().description("The 'source' field of the new message, defaults to '$message.source'").build();
        timestampParam = type(TIMESTAMP_ARG, DateTime.class).optional().description("The 'timestamp' field of the message, defaults to 'now'").build();
    }

    @Override
    public Message evaluate(FunctionArgs args, EvaluationContext context) {
        final Optional<String> optMessage = messageParam.optional(args, context);
        final String message = optMessage.isPresent() ? optMessage.get() : context.currentMessage().getMessage();

        final Optional<String> optSource = sourceParam.optional(args, context);
        final String source = optSource.isPresent() ? optSource.get() : context.currentMessage().getSource();

        final Optional<DateTime> optTimestamp = timestampParam.optional(args, context);
        final DateTime timestamp = optTimestamp.isPresent() ? optTimestamp.get() : Tools.nowUTC();

        final Message newMessage = new Message(message, source, timestamp);

        // register in context so the processor can inject it later on
        context.addCreatedMessage(newMessage);
        return newMessage;
    }

    @Override
    public FunctionDescriptor<Message> descriptor() {
        return FunctionDescriptor.<Message>builder()
                .name(NAME)
                .returnType(Message.class)
                .params(of(
                        messageParam,
                        sourceParam,
                        timestampParam
                ))
                .description("Creates a new message")
                .build();
    }
}
