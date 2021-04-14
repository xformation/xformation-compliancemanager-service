/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.conversion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import java.util.Collections;
import java.util.Map;

import static com.google.common.collect.ImmutableList.of;
import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.object;

public class MapConversion extends AbstractFunction<Map> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static final String NAME = "to_map";
    private static final String VALUE = "value";

    private final ParameterDescriptor<Object, Object> valueParam;


    public MapConversion() {
        this.valueParam = object(VALUE).description("Map-like value to convert").build();
    }

    @Override
    public Map evaluate(FunctionArgs args, EvaluationContext context) {
        final Object value = valueParam.required(args, context);

        if (value == null) {
            return Collections.emptyMap();
        } else if (value instanceof Map) {
            return (Map) value;
        } else if (value instanceof JsonNode) {
            final JsonNode jsonNode = (JsonNode) value;
            return MAPPER.convertValue(jsonNode, Map.class);
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public FunctionDescriptor<Map> descriptor() {
        return FunctionDescriptor.<Map>builder()
                .name(NAME)
                .returnType(Map.class)
                .params(of(valueParam))
                .description("Converts a map-like value into a map usable by set_fields()")
                .build();
    }
}
