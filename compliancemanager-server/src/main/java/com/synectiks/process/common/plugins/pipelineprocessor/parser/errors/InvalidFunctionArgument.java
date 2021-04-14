/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.parser.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.exceptions.PrecomputeFailure;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.Function;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import com.synectiks.process.common.plugins.pipelineprocessor.parser.RuleLangParser;

public class InvalidFunctionArgument extends ParseError {
    private final Function<?> function;
    private final PrecomputeFailure failure;

    public InvalidFunctionArgument(RuleLangParser.FunctionCallContext ctx,
                                   Function<?> function,
                                   PrecomputeFailure failure) {
        super("invalid_function_argument", ctx);
        this.function = function;
        this.failure = failure;
    }

    @JsonProperty("reason")
    @Override
    public String toString() {
        int paramPosition = 1;
        for (ParameterDescriptor descriptor : function.descriptor().params()) {
            if (descriptor.name().equals(failure.getArgumentName())) {
                break;
            }
            paramPosition++;
        }

        return "Unable to pre-compute value for " + ordinal(paramPosition) + " argument " + failure.getArgumentName() + " in call to function " + function.descriptor().name() + ": " + failure.getCause().getMessage();
    }

    private static String ordinal(int i) {
        String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + suffixes[i % 10];

        }
    }
}
