/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import org.antlr.v4.runtime.Token;
import org.joda.time.DateTime;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;

public class ComparisonExpression extends BinaryExpression implements LogicalExpression {
    private final String operator;

    public ComparisonExpression(Token start, Expression left, Expression right, String operator) {
        super(start, left, right);
        this.operator = operator;
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

        final Object leftValue = this.left.evaluateUnsafe(context);
        final Object rightValue = this.right.evaluateUnsafe(context);
        if (leftValue instanceof DateTime && rightValue instanceof DateTime) {
            return compareDateTimes(operator, (DateTime) leftValue, (DateTime) rightValue);
        }

        if (leftValue instanceof Double || rightValue instanceof Double) {
            return compareDouble(operator, (double) leftValue, (double) rightValue);
        }

        return compareLong(operator, (long) leftValue, (long) rightValue);
    }

    @SuppressWarnings("Duplicates")
    private boolean compareLong(String operator, long left, long right) {
        switch (operator) {
            case ">":
                return left > right;
            case ">=":
                return left >= right;
            case "<":
                return left < right;
            case "<=":
                return left <= right;
            default:
                return false;
        }
    }

    @SuppressWarnings("Duplicates")
    private boolean compareDouble(String operator, double left, double right) {
        switch (operator) {
            case ">":
                return left > right;
            case ">=":
                return left >= right;
            case "<":
                return left < right;
            case "<=":
                return left <= right;
            default:
                return false;
        }
    }

    private boolean compareDateTimes(String operator, DateTime left, DateTime right) {
        switch (operator) {
            case ">":
                return left.isAfter(right);
            case ">=":
                return !left.isBefore(right);
            case "<":
                return left.isBefore(right);
            case "<=":
                return !left.isAfter(right);
            default:
                return false;
        }
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return left.toString() + " " + operator + " " + right.toString();
    }
}
