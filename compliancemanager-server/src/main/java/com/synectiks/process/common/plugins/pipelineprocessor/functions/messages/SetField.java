/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.messages;

import com.google.common.base.Strings;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;
import com.synectiks.process.server.plugin.Message;

import java.util.Optional;

import static com.google.common.collect.ImmutableList.of;
import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.object;
import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.string;
import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.type;

public class SetField extends AbstractFunction<Void> {

    public static final String NAME = "set_field";

    private final ParameterDescriptor<String, String> fieldParam;
    private final ParameterDescriptor<Object, Object> valueParam;
    private final ParameterDescriptor<String, String> prefixParam;
    private final ParameterDescriptor<String, String> suffixParam;
    private final ParameterDescriptor<Message, Message> messageParam;

    public SetField() {
        fieldParam = string("field").description("The new field name").build();
        valueParam = object("value").description("The new field value").build();
        prefixParam = string("prefix").optional().description("The prefix for the field name").build();
        suffixParam = string("suffix").optional().description("The suffix for the field name").build();
        messageParam = type("message", Message.class).optional().description("The message to use, defaults to '$message'").build();
    }

    @Override
    public Void evaluate(FunctionArgs args, EvaluationContext context) {
        String field = fieldParam.required(args, context);
        final Object value = valueParam.required(args, context);

        if (!Strings.isNullOrEmpty(field)) {
            final Message message = messageParam.optional(args, context).orElse(context.currentMessage());
            final Optional<String> prefix = prefixParam.optional(args, context);
            final Optional<String> suffix = suffixParam.optional(args, context);

            if (prefix.isPresent()) {
                field = prefix.get() + field;
            }
            if (suffix.isPresent()) {
                field = field + suffix.get();
            }
            message.addField(field, value);
        }
        return null;
    }

    @Override
    public FunctionDescriptor<Void> descriptor() {
        return FunctionDescriptor.<Void>builder()
                .name(NAME)
                .returnType(Void.class)
                .params(of(fieldParam,
                           valueParam,
                           prefixParam,
                           suffixParam,
                           messageParam))
                .description("Sets a new field in a message")
                .build();
    }
}
