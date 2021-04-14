/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.parser.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.Expression;

import org.antlr.v4.runtime.ParserRuleContext;

public class InvalidOperation extends ParseError {
    private final Expression expr;

    private final String message;

    public InvalidOperation(ParserRuleContext ctx, Expression expr, String message) {
        super("invalid_operation", ctx);
        this.expr = expr;
        this.message = message;
    }

    @JsonProperty("reason")
    @Override
    public String toString() {
        return "Invalid operation: " + message;
    }

    public Expression getExpression() {
        return expr;
    }

    public String getMessage() {
        return message;
    }
}
