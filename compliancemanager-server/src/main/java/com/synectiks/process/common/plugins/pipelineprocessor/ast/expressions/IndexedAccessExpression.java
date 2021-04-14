/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;

import org.antlr.v4.runtime.Token;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

public class IndexedAccessExpression extends BaseExpression {
    private final Expression indexableObject;
    private final Expression index;

    public IndexedAccessExpression(Token start, Expression indexableObject, Expression index) {
        super(start);
        this.indexableObject = indexableObject;
        this.index = index;
    }

    @Override
    public boolean isConstant() {
        return indexableObject.isConstant() && index.isConstant();
    }

    @Override
    public Object evaluateUnsafe(EvaluationContext context) {
        final Object idxObj = this.index.evaluateUnsafe(context);
        final Object indexable = indexableObject.evaluateUnsafe(context);
        if (idxObj == null || indexable == null) {
            return null;
        }

        if (idxObj instanceof Long) {
            int idx = Ints.saturatedCast((long) idxObj);
            if (indexable.getClass().isArray()) {
                return Array.get(indexable, idx);
            } else if (indexable instanceof List) {
                return ((List) indexable).get(idx);
            } else if (indexable instanceof Iterable) {
                return Iterables.get((Iterable) indexable, idx);
            }
            throw new IllegalArgumentException("Object '" + indexable + "' is not an Array, List or Iterable.");
        } else if (idxObj instanceof String) {
            final String idx = idxObj.toString();
            if (indexable instanceof Map) {
                return ((Map) indexable).get(idx);
            }
            throw new IllegalArgumentException("Object '" + indexable + "' is not a Map.");
        }
        throw new IllegalArgumentException("Index '" + idxObj + "' is not a Long or String.");
    }

    @Override
    public Class getType() {
        return Object.class;
    }

    @Override
    public String toString() {
        return indexableObject.toString() + "[" + index.toString() + "]";
    }

    public Expression getIndexableObject() {
        return indexableObject;
    }

    public Expression getIndex() {
        return index;
    }

    @Override
    public Iterable<Expression> children() {
        return ImmutableList.of(indexableObject, index);
    }
}
