/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import static com.google.common.collect.ImmutableList.of;

public class IsNull extends AbstractFunction<Boolean> {

    public static final String NAME = "is_null";
    private final ParameterDescriptor<Object, Object> valueParam;

    public IsNull() {
        valueParam = ParameterDescriptor.type("value", Object.class).description("The value to check").build();
    }

    @Override
    public Boolean evaluate(FunctionArgs args, EvaluationContext context) {
        try {
            final Object value = valueParam.required(args, context);
            return value == null;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public FunctionDescriptor<Boolean> descriptor() {
        return FunctionDescriptor.<Boolean>builder()
                .name(NAME)
                .returnType(Boolean.class)
                .params(of(valueParam))
                .description("Checks whether a value is 'null'")
                .build();
    }
}
