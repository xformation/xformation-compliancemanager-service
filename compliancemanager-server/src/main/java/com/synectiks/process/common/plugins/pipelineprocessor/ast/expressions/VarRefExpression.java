/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import org.antlr.v4.runtime.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;

import java.util.Collections;

public class VarRefExpression extends BaseExpression {
    private static final Logger log = LoggerFactory.getLogger(VarRefExpression.class);
    private final String identifier;
    private final Expression varExpr;
    private Class type = Object.class;

    public VarRefExpression(Token start, String identifier, Expression varExpr) {
        super(start);
        this.identifier = identifier;
        this.varExpr = varExpr;
    }

    @Override
    public boolean isConstant() {
        return varExpr != null && varExpr.isConstant();
    }

    @Override
    public Object evaluateUnsafe(EvaluationContext context) {
        final EvaluationContext.TypedValue typedValue = context.get(identifier);
        if (typedValue != null) {
            return typedValue.getValue();
        }
        log.error("Unable to retrieve value for variable {}", identifier);
        return null;
    }

    @Override
    public Class getType() {
        return type;
    }

    @Override
    public String toString() {
        return identifier;
    }

    public String varName() {
        return identifier;
    }

    public Expression varExpr() { return varExpr; }

    public void setType(Class type) {
        this.type = type;
    }

    @Override
    public Iterable<Expression> children() {
        return Collections.emptySet();
    }
}
