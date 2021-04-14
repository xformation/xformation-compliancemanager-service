/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.codegen.compiler;

import java.net.URI;

import javax.tools.SimpleJavaFileObject;
import javax.validation.constraints.NotNull;

import static javax.tools.JavaFileObject.Kind.SOURCE;

public class JavaSourceFromString extends SimpleJavaFileObject {

    private final String sourceCode;

    public JavaSourceFromString(@NotNull String name, String sourceCode) {
        super(URI.create("string:///" + name.replace('.', '/') + SOURCE.extension), SOURCE);
        this.sourceCode = sourceCode;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return sourceCode;
    }
}
