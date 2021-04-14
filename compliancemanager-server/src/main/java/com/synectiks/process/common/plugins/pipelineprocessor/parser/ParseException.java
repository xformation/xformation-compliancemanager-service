/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.parser;

import java.util.Set;

import com.synectiks.process.common.plugins.pipelineprocessor.parser.errors.ParseError;

public class ParseException extends RuntimeException {
    private final Set<ParseError> errors;

    public ParseException(Set<ParseError> errors) {
        this.errors = errors;
    }

    public Set<ParseError> getErrors() {
        return errors;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder("Errors:\n");
        for (ParseError parseError : getErrors()) {
            sb.append(" ").append(parseError).append("\n");
        }
        return sb.toString();
    }
}
