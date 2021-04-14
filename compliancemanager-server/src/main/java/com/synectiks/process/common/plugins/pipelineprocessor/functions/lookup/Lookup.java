/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.lookup;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;
import com.synectiks.process.server.lookup.LookupTableService;
import com.synectiks.process.server.plugin.lookup.LookupResult;

import java.util.Collections;
import java.util.Map;

import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.object;
import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.string;
import static com.synectiks.process.server.plugin.lookup.LookupResult.SINGLE_VALUE_KEY;

public class Lookup extends AbstractFunction<Map<Object, Object>> {

    public static final String NAME = "lookup";

    private final ParameterDescriptor<String, LookupTableService.Function> lookupTableParam;
    private final ParameterDescriptor<Object, Object> keyParam;
    private final ParameterDescriptor<Object, Object> defaultParam;

    @Inject
    public Lookup(LookupTableService lookupTableService) {
        lookupTableParam = string("lookup_table", LookupTableService.Function.class)
                .description("The existing lookup table to use to lookup the given key")
                .transform(tableName -> lookupTableService.newBuilder().lookupTable(tableName).build())
                .build();
        keyParam = object("key")
                .description("The key to lookup in the table")
                .build();
        defaultParam = object("default")
                .description("The default multi value that should be used if there is no lookup result")
                .optional()
                .build();
    }

    @Override
    public Map<Object, Object> evaluate(FunctionArgs args, EvaluationContext context) {
        Object key = keyParam.required(args, context);
        if (key == null) {
            return Collections.singletonMap(SINGLE_VALUE_KEY, defaultParam.optional(args, context).orElse(null));
        }
        LookupTableService.Function table = lookupTableParam.required(args, context);
        if (table == null) {
            return Collections.singletonMap(SINGLE_VALUE_KEY, defaultParam.optional(args, context).orElse(null));
        }
        LookupResult result = table.lookup(key);
        if (result == null || result.isEmpty()) {
            return Collections.singletonMap(SINGLE_VALUE_KEY, defaultParam.optional(args, context).orElse(null));
        }
        return result.multiValue();
    }

    @Override
    public FunctionDescriptor<Map<Object, Object>> descriptor() {
        //noinspection unchecked
        return FunctionDescriptor.<Map<Object, Object>>builder()
                .name(NAME)
                .description("Looks up a multi value in the named lookup table.")
                .params(lookupTableParam, keyParam, defaultParam)
                .returnType((Class<? extends Map<Object, Object>>) new TypeLiteral<Map<Object, Object>>() {}.getRawType())
                .build();
    }
}
