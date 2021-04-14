/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.parser.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.Function;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import com.synectiks.process.common.plugins.pipelineprocessor.parser.RuleLangParser;

public class MissingRequiredParam extends ParseError {
    private final Function<?> function;
    private final ParameterDescriptor param;

    public MissingRequiredParam(RuleLangParser.FunctionCallContext ctx,
                                Function<?> function,
                                ParameterDescriptor param) {
        super("missing_required_param", ctx);
        this.function = function;
        this.param = param;
    }

    @JsonProperty("reason")
    @Override
    public String toString() {
        return "Missing required parameter " + param.name() +
                " of type " + param.type().getSimpleName() +
                " in call to function " + function.descriptor().name()
                + positionString();
    }
}
