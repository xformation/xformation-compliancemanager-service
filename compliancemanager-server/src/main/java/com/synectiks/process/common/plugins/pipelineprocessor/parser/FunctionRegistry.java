/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.parser;

import javax.inject.Inject;

import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.Function;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class FunctionRegistry {

    private final Map<String, Function<?>> functions;

    @Inject
    public FunctionRegistry(Map<String, Function<?>> functions) {
        this.functions = functions;
    }


    public Function<?> resolve(String name) {
        return functions.get(name);
    }

    public Function<?> resolveOrError(String name) {
        final Function<?> function = resolve(name);
        if (function == null) {
            return Function.ERROR_FUNCTION;
        }
        return function;
    }

    public Collection<Function<?>> all() {
        return functions.values().stream().collect(Collectors.toList());
    }
}
