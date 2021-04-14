/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.parser.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.Function;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import com.synectiks.process.common.plugins.pipelineprocessor.parser.RuleLangParser;

import java.util.function.Predicate;

public class WrongNumberOfArgs extends ParseError {
    private final Function<?> function;
    private final int argCount;

    public WrongNumberOfArgs(RuleLangParser.FunctionCallContext ctx,
                             Function<?> function,
                             int argCount) {
        super("wrong_number_of_arguments", ctx);
        this.function = function;
        this.argCount = argCount;
    }

    @JsonProperty("reason")
    @Override
    public String toString() {
        final Predicate<ParameterDescriptor> optional = ParameterDescriptor::optional;
        return "Expected " + function.descriptor().params().stream().filter(optional.negate()).count() +
                " arguments but found " + argCount +
                " in call to function " + function.descriptor().name()
                + positionString();
    }
}
