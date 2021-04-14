/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.ConstantExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.Expression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.IndexedAccessExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.LongExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.StringExpression;
import com.synectiks.process.server.plugin.Message;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class IndexedAccessExpressionTest {

    public static final Token START = new CommonToken(-1);
    private EvaluationContext context;

    @Before
    public void setup() {
        context = new EvaluationContext(new Message("test message", "test", DateTime.parse("2010-07-30T16:03:25Z")));
    }

    @Test
    public void accessArray() {
        int ary[] = new int[] {23};
        final IndexedAccessExpression idxExpr = new IndexedAccessExpression(START, obj(ary), num(0));

        final Object evaluate = idxExpr.evaluateUnsafe(context);
        assertThat(evaluate).isOfAnyClassIn(Integer.class);
        assertThat(evaluate).isEqualTo(23);
    }

    @Test
    public void accessList() {
        final ImmutableList<Integer> list = ImmutableList.of(23);
        final IndexedAccessExpression idxExpr = new IndexedAccessExpression(START, obj(list), num(0));

        final Object evaluate = idxExpr.evaluateUnsafe(context);
        assertThat(evaluate).isOfAnyClassIn(Integer.class);
        assertThat(evaluate).isEqualTo(23);
    }

    @Test
    public void accessIterable() {
        final Iterable<Integer> iterable = () -> new AbstractIterator<Integer>() {
            private boolean done = false;

            @Override
            protected Integer computeNext() {
                if (done) {
                    return endOfData();
                }
                done = true;
                return 23;
            }
        };
        final IndexedAccessExpression idxExpr = new IndexedAccessExpression(START, obj(iterable), num(0));

        final Object evaluate = idxExpr.evaluateUnsafe(context);
        assertThat(evaluate).isOfAnyClassIn(Integer.class);
        assertThat(evaluate).isEqualTo(23);
    }

    @Test
    public void accessMap() {
        final ImmutableMap<String, Integer> map = ImmutableMap.of("string", 23);
        final IndexedAccessExpression idxExpr = new IndexedAccessExpression(START, obj(map), string("string"));

        final Object evaluate = idxExpr.evaluateUnsafe(context);
        assertThat(evaluate).isEqualTo(23);
    }

    @Test
    public void invalidObject() {
        final IndexedAccessExpression expression = new IndexedAccessExpression(START, obj(23), num(0));

        // this should throw an exception
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> expression.evaluateUnsafe(context));
    }

    private static Expression num(long idx) {
        return new LongExpression(START, idx);
    }

    private static Expression string(String string) {
        return new StringExpression(START, string);
    }

    private static ConstantObjectExpression obj(Object object) {
        return new ConstantObjectExpression(object);
    }

    private static class ConstantObjectExpression extends ConstantExpression {
        private final Object object;

        protected ConstantObjectExpression(Object object) {
            super(START, object.getClass());
            this.object = object;
        }

        @Override
        public Object evaluateUnsafe(EvaluationContext context) {
            return object;
        }
    }
}
