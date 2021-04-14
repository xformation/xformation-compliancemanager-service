/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.parser.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synectiks.process.common.plugins.pipelineprocessor.parser.RuleLangParser;

public class IncompatibleType extends ParseError {
    private final Class<?> expected;
    private final Class<?> actual;

    public IncompatibleType(RuleLangParser.ExpressionContext ctx, Class<?> expected, Class<?> actual) {
        super("incompatible_type", ctx);
        this.expected = expected;
        this.actual = actual;
    }

    @JsonProperty("reason")
    @Override
    public String toString() {
        return "Expected type " + expected.getSimpleName() + " but found " + actual.getSimpleName() + positionString();
    }
}
