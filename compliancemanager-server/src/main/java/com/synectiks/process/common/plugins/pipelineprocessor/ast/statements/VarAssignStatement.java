/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.statements;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.Expression;

public class VarAssignStatement implements Statement {
    private final String name;
    private final Expression expr;

    public VarAssignStatement(String name, Expression expr) {
        this.name = name;
        this.expr = expr;
    }

    @Override
    public Void evaluate(EvaluationContext context) {
        final Object result = expr.evaluate(context);
        context.define(name, expr.getType(), result);
        return null;
    }

    public String getName() {
        return name;
    }

    public Expression getValueExpression() {
        return expr;
    }

    @Override
    public String toString() {
        return "let " + name + " = " + expr.toString();
    }
}
