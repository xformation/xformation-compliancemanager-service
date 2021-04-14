/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.codegen.compiler;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.tools.Diagnostic;

public class PipelineCompilationException extends RuntimeException {
    private final List<Diagnostic> errors;

    public PipelineCompilationException(List<Diagnostic> errors) {
        this.errors = errors;
    }

    @Override
    public String getMessage() {
        return errors.stream()
                .map(diagnostic -> diagnostic.getMessage(Locale.ENGLISH))
                .collect(Collectors.joining("\n"));
    }
}
