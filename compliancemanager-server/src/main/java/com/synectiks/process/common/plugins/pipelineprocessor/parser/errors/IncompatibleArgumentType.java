/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.parser.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.Expression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.FunctionExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import com.synectiks.process.common.plugins.pipelineprocessor.parser.RuleLangParser;

public class IncompatibleArgumentType extends ParseError {
    private final FunctionExpression functionExpression;
    private final ParameterDescriptor p;
    private final Expression argExpr;

    public IncompatibleArgumentType(RuleLangParser.FunctionCallContext ctx,
                                    FunctionExpression functionExpression,
                                    ParameterDescriptor p,
                                    Expression argExpr) {
        super("incompatible_argument_type", ctx);
        this.functionExpression = functionExpression;
        this.p = p;
        this.argExpr = argExpr;
    }

    @JsonProperty("reason")
    @Override
    public String toString() {
        return "Expected type " + p.type().getSimpleName() +
                " for argument " + p.name() +
                " but found " + argExpr.getType().getSimpleName() +
                " in call to function " + functionExpression.getFunction().descriptor().name()
                + positionString();
    }
}
