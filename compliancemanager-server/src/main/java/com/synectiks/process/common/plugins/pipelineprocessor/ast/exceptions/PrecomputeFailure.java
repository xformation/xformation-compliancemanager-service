/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.exceptions;

public class PrecomputeFailure extends RuntimeException {
    private final String argumentName;

    public PrecomputeFailure(String argumentName, Exception cause) {
        super(cause);
        this.argumentName = argumentName;
    }

    public String getArgumentName() {
        return argumentName;
    }

    @Override
    public String getMessage() {
        return "Unable to pre-compute argument " + getArgumentName() + ": " + getCause().getMessage();
    }
}
