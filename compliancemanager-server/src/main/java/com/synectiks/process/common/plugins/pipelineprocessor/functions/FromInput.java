/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;
import com.synectiks.process.server.plugin.IOState;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.shared.inputs.InputRegistry;

import javax.inject.Inject;

import static com.google.common.collect.ImmutableList.of;
import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.string;

public class FromInput extends AbstractFunction<Boolean> {

    public static final String NAME = "from_input";
    public static final String ID_ARG = "id";
    public static final String NAME_ARG = "name";

    private final InputRegistry inputRegistry;
    private final ParameterDescriptor<String, String> idParam;
    private final ParameterDescriptor<String, String> nameParam;

    @Inject
    public FromInput(InputRegistry inputRegistry) {
        this.inputRegistry = inputRegistry;
        idParam = string(ID_ARG).optional().description("The input's ID, this is much faster than 'name'").build();
        nameParam = string(NAME_ARG).optional().description("The input's name").build();
    }

    @Override
    public Boolean evaluate(FunctionArgs args, EvaluationContext context) {
        String id = idParam.optional(args, context).orElse("");

        MessageInput input = null;
        if ("".equals(id)) {
            final String name = nameParam.optional(args, context).orElse("");
            for (IOState<MessageInput> messageInputIOState : inputRegistry.getInputStates()) {
                final MessageInput messageInput = messageInputIOState.getStoppable();
                if (messageInput.getTitle().equalsIgnoreCase(name)) {
                    input = messageInput;
                    break;
                }
            }
            if ("".equals(name)) {
                return null;
            }
        } else {
            final IOState<MessageInput> inputState = inputRegistry.getInputState(id);
            if (inputState != null) {
                input = inputState.getStoppable();
            }

        }
        return input != null
                && input.getId().equals(context.currentMessage().getSourceInputId());
    }

    @Override
    public FunctionDescriptor<Boolean> descriptor() {
        return FunctionDescriptor.<Boolean>builder()
                .name(NAME)
                .returnType(Boolean.class)
                .params(of(
                        idParam,
                        nameParam))
                .description("Checks if a message arrived on a given input")
                .build();
    }
}
