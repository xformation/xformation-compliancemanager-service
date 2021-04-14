/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.messages;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;
import com.synectiks.process.server.plugin.Message;

import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.type;

public class TrafficAccountingSize extends AbstractFunction<Long> {
    public static final String NAME = "traffic_accounting_size";
    public static final String MESSAGE_ARG = "message";
    private final ParameterDescriptor<Message, Message> messageParam;

    public TrafficAccountingSize() {
        messageParam = type(MESSAGE_ARG, Message.class).optional().description("The message to get the current accounting size for, defaults to '$message'").build();
    }

    @Override
    public Long evaluate(FunctionArgs args, EvaluationContext context) {
        final Message message = messageParam.optional(args, context).orElse(context.currentMessage());
        final long size = message.getSize();
        return size;
    }

    @Override
    public FunctionDescriptor<Long> descriptor() {
        return FunctionDescriptor.<Long>builder()
                .name(NAME)
                .returnType(Long.class)
                .params(ImmutableList.of(messageParam))
                .description("Calculates the current size of the message as used by the traffic accounting system.")
                .build();
    }
}
