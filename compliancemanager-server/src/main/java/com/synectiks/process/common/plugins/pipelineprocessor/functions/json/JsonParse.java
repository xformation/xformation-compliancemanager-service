/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.io.IOException;

import static com.google.common.collect.ImmutableList.of;

public class JsonParse extends AbstractFunction<JsonNode> {
    private static final Logger log = LoggerFactory.getLogger(JsonParse.class);
    public static final String NAME = "parse_json";

    private final ObjectMapper objectMapper;
    private final ParameterDescriptor<String, String> valueParam;

    @Inject
    public JsonParse(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        valueParam = ParameterDescriptor.string("value").description("The string to parse as a JSON tree").build();
    }

    @Override
    public JsonNode evaluate(FunctionArgs args, EvaluationContext context) {
        final String value = valueParam.required(args, context);
        try {
            final JsonNode node = objectMapper.readTree(value);
            if (node == null) {
                throw new IOException("null result");
            }
            return node;
        } catch (IOException e) {
            log.warn("Unable to parse JSON", e);
        }
        return MissingNode.getInstance();
    }

    @Override
    public FunctionDescriptor<JsonNode> descriptor() {
        return FunctionDescriptor.<JsonNode>builder()
                .name(NAME)
                .returnType(JsonNode.class)
                .params(of(
                        valueParam
                ))
                .description("Parses a string as a JSON tree")
                .build();
    }
}
