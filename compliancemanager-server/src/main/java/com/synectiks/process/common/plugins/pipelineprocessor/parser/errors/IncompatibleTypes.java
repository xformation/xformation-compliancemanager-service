/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.parser.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.BinaryExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.Expression;

import com.synectiks.process.common.plugins.pipelineprocessor.parser.RuleLangParser;

public class IncompatibleTypes extends ParseError {
    private final RuleLangParser.ExpressionContext ctx;
    private final BinaryExpression binaryExpr;

    public IncompatibleTypes(RuleLangParser.ExpressionContext ctx, BinaryExpression binaryExpr) {
        super("incompatible_types", ctx);
        this.ctx = ctx;
        this.binaryExpr = binaryExpr;
    }

    @JsonProperty("reason")
    @Override
    public String toString() {
        return "Incompatible types " + exprString(binaryExpr.left()) + " <=> " + exprString(binaryExpr.right()) + positionString();
    }

    private String exprString(Expression e) {
        return "(" + e.toString() + ") : " + e.getType().getSimpleName();
    }


}
