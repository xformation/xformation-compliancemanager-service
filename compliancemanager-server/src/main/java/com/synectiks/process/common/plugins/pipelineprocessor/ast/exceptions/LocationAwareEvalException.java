/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.exceptions;

import org.antlr.v4.runtime.Token;

public class LocationAwareEvalException extends RuntimeException {
    private final Token startToken;

    public LocationAwareEvalException(Token startToken, Throwable cause) {
        super(cause);
        this.startToken = startToken;
    }

    public Token getStartToken() {
        return startToken;
    }
}
