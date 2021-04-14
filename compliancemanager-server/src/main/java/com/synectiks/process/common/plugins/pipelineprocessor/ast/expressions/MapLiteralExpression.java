/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;

import org.antlr.v4.runtime.Token;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;

import java.util.HashMap;
import java.util.Map;

public class MapLiteralExpression extends BaseExpression {
    private final HashMap<String, Expression> map;

    public MapLiteralExpression(Token start, HashMap<String, Expression> map) {
        super(start);
        this.map = map;
    }

    @Override
    public boolean isConstant() {
        return map.values().stream().allMatch(Expression::isConstant);
    }

    @Override
    public Map evaluateUnsafe(EvaluationContext context) {
        // evaluate all values for each key and return the resulting map
        return Seq.seq(map)
                .map(entry -> entry.map2(value -> value.evaluateUnsafe(context)))
                .toMap(Tuple2::v1, Tuple2::v2);
    }

    @Override
    public Class getType() {
        return Map.class;
    }

    @Override
    public String toString() {
        return "{" + Joiner.on(", ").withKeyValueSeparator(":").join(map) + "}";
    }

    public Iterable<Map.Entry<String, Expression>> entries() {
        return ImmutableSet.copyOf(map.entrySet());
    }

    @Override
    public Iterable<Expression> children() {
        return ImmutableList.copyOf(map.values());
    }
}
