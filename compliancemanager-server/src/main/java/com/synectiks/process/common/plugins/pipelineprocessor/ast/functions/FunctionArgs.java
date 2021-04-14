/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.functions;

import com.google.common.collect.Maps;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.Expression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.VarRefExpression;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.MoreObjects.firstNonNull;

public class FunctionArgs {

    @Nonnull
    private final Map<String, Expression> args;

    private final Map<String, Object> constantValues = Maps.newHashMap();
    private final Function function;
    private final FunctionDescriptor descriptor;

    public FunctionArgs(Function func, Map<String, Expression> args) {
        function = func;
        descriptor = function.descriptor();
        this.args = firstNonNull(args, Collections.<String, Expression>emptyMap());
    }

    @Nonnull
    public Map<String, Expression> getArgs() {
        return args;
    }

    @Nonnull
    public Map<String, Expression> getConstantArgs() {
        return args.entrySet().stream()
                .filter(e -> e != null && e.getValue() != null && e.getValue().isConstant())
                .filter(e -> !(e.getValue() instanceof VarRefExpression)) // do not eagerly touch variables
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public boolean isPresent(String key) {
        return args.containsKey(key);
    }

    @Nullable
    public Expression expression(String key) {
        return args.get(key);
    }

    public Object getPreComputedValue(String name) {
        return constantValues.get(name);
    }

    public void setPreComputedValue(@Nonnull String name, @Nonnull Object value) {
        Objects.requireNonNull(value);
        constantValues.put(name, value);
    }

    public Function<?> getFunction() {
        return function;
    }

    public ParameterDescriptor<?, ?> param(String name) {
        return descriptor.param(name);
    }
}
