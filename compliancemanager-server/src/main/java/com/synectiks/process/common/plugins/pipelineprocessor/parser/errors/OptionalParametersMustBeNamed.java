/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.parser.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.Function;

import com.synectiks.process.common.plugins.pipelineprocessor.parser.RuleLangParser;

public class OptionalParametersMustBeNamed extends ParseError {
    private final Function<?> function;

    public OptionalParametersMustBeNamed(RuleLangParser.FunctionCallContext ctx, Function<?> function) {
        super("must_name_optional_params", ctx);
        this.function = function;
    }

    @JsonProperty("reason")
    @Override
    public String toString() {
        return "Function " + function.descriptor().name() + " has optional parameters, must use named parameters to call" + positionString();
    }
}
