/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import com.google.common.base.Joiner;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.exceptions.FunctionEvaluationException;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.exceptions.LocationAwareEvalException;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.Function;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;

import org.antlr.v4.runtime.Token;

import java.util.Map;
import java.util.stream.Collectors;

public class FunctionExpression extends BaseExpression {
    private final FunctionArgs args;
    private final Function<?> function;
    private final FunctionDescriptor descriptor;

    public FunctionExpression(Token start, FunctionArgs args) {
        super(start);
        this.args = args;
        this.function = args.getFunction();
        this.descriptor = this.function.descriptor();

        // precomputes all constant arguments to avoid dynamically recomputing trees on every invocation
        this.function.preprocessArgs(args);
    }

    public Function<?> getFunction() {
        return function;
    }

    public FunctionArgs getArgs() {
        return args;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public Object evaluateUnsafe(EvaluationContext context) {
        try {
            return descriptor.returnType().cast(function.evaluate(args, context));
        } catch (LocationAwareEvalException laee) {
            // the exception already has a location from the input source, simply propagate it.
            throw laee;
        } catch (Exception e) {
            // we need to wrap the original exception to retain the position in the tree where the exception originated
            throw new FunctionEvaluationException(this, e);
        }
    }

    @Override
    public Class getType() {
        return descriptor.returnType();
    }

    @Override
    public String toString() {
        String argsString = "";
        if (args != null) {
            argsString = Joiner.on(", ")
                    .withKeyValueSeparator(": ")
                    .join(args.getArgs().entrySet().stream()
                                  .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                                  .iterator());
        }
        return descriptor.name() + "(" + argsString + ")";
    }

    @Override
    public Iterable<Expression> children() {
        return args.getArgs().entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }
}
