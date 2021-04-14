/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import org.antlr.v4.runtime.Token;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;

public class EqualityExpression extends BinaryExpression implements LogicalExpression {
    private static final Logger log = LoggerFactory.getLogger(EqualityExpression.class);

    private final boolean checkEquality;

    public EqualityExpression(Token start, Expression left, Expression right, boolean checkEquality) {
        super(start, left, right);
        this.checkEquality = checkEquality;
    }

    @Override
    public Object evaluateUnsafe(EvaluationContext context) {
        return evaluateBool(context);
    }

    @Override
    public Class getType() {
        return Boolean.class;
    }

    @Override
    public boolean evaluateBool(EvaluationContext context) {
        final Object left = this.left.evaluateUnsafe(context);
        final Object right = this.right.evaluateUnsafe(context);
        if (left == null) {
            log.warn("left expression evaluated to null, returning false: {}", this.left);
            return false;
        }
        final boolean equals;
        // sigh: DateTime::equals takes the chronology into account, so identical instants expressed in different timezones are not equal
        if (left instanceof DateTime && right instanceof DateTime) {
            equals = ((DateTime) left).isEqual((DateTime) right);
        } else {
            equals = left.equals(right);
        }

        if (log.isTraceEnabled()) {
            traceEquality(left, right, equals, checkEquality);
        }
        if (checkEquality) {
            return equals;
        }
        return !equals;
    }

    private void traceEquality(Object left,
                               Object right,
                               boolean equals,
                               boolean checkEquality) {
        log.trace(checkEquality
                          ? "[{}] {} == {} : {} == {}"
                          : "[{}] {} != {} : {} != {}",
                  checkEquality == equals, this.left, this.right, left, right);
    }

    public boolean isCheckEquality() {
        return checkEquality;
    }

    @Override
    public String toString() {
        return left.toString() + (checkEquality ? " == " : " != ") + right.toString();
    }
}
