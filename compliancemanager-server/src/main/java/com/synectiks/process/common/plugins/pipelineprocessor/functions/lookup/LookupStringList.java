/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.lookup;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;
import com.synectiks.process.server.lookup.LookupTableService;
import com.synectiks.process.server.plugin.lookup.LookupResult;

import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.object;
import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.string;

import java.util.List;

public class LookupStringList extends AbstractFunction<List<String>> {

    public static final String NAME = "lookup_string_list";

    private final ParameterDescriptor<String, LookupTableService.Function> lookupTableParam;
    private final ParameterDescriptor<Object, Object> keyParam;
    @SuppressWarnings("rawtypes") // we cannot store class instances of generic types
    private final ParameterDescriptor<List, List> defaultParam;

    @Inject
    public LookupStringList(LookupTableService lookupTableService) {
        lookupTableParam = string("lookup_table", LookupTableService.Function.class)
                .description("The existing lookup table to use to lookup the given key")
                .transform(tableName -> lookupTableService.newBuilder().lookupTable(tableName).build())
                .build();
        keyParam = object("key")
                .description("The key to lookup in the table")
                .build();
        defaultParam = ParameterDescriptor.type("default", List.class)
                .description("The default list value that should be used if there is no lookup result")
                .optional()
                .build();
    }

    @Override
    public List<String> evaluate(FunctionArgs args, EvaluationContext context) {
        Object key = keyParam.required(args, context);
        if (key == null) {
            //noinspection unchecked
            return defaultParam.optional(args, context).orElse(ImmutableList.of());
        }
        LookupTableService.Function table = lookupTableParam.required(args, context);
        if (table == null) {
            //noinspection unchecked
            return defaultParam.optional(args, context).orElse(ImmutableList.of());
        }
        LookupResult result = table.lookup(key);
        if (result == null || result.isEmpty()) {
            //noinspection unchecked
            return defaultParam.optional(args, context).orElse(ImmutableList.of());
        }
        return result.stringListValue();
    }

    @Override
    public FunctionDescriptor<List<String>> descriptor() {
        //noinspection unchecked
        return FunctionDescriptor.<List<String>>builder()
                .name(NAME)
                .description("Looks up a string list value in the named lookup table.")
                .params(lookupTableParam, keyParam, defaultParam)
                .returnType((Class<? extends List<String>>) new TypeLiteral<List<String>>() {}.getRawType())
                .build();
    }
}
