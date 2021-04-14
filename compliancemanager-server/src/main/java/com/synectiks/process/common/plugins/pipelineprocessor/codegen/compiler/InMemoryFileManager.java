/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.codegen.compiler;

import com.google.common.collect.Maps;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
    private final Map<String, ByteArrayOutputStream> classBytes = Maps.newLinkedHashMap();

    public InMemoryFileManager(StandardJavaFileManager fileManager) {
        super(fileManager);
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind) throws IOException {
        if (location == StandardLocation.CLASS_OUTPUT && classBytes.containsKey(className) && kind == Kind.CLASS) {
            final byte[] bytes = classBytes.get(className).toByteArray();
            return new SimpleJavaFileObject(URI.create(className), kind) {
                @Override
                @NotNull
                public InputStream openInputStream() {
                    return new ByteArrayInputStream(bytes);
                }
            };
        }
        return fileManager.getJavaFileForInput(location, className, kind);
    }

    @Override
    @NotNull
    public JavaFileObject getJavaFileForOutput(Location location, final String className, Kind kind, FileObject sibling) throws IOException {
        return new SimpleJavaFileObject(URI.create(className), kind) {
            @Override
            @NotNull
            public OutputStream openOutputStream() {
                final ByteArrayOutputStream stream = new ByteArrayOutputStream();
                classBytes.put(className, stream);
                return stream;
            }
        };
    }

    @NotNull
    public Map<String, byte[]> getAllClassBytes() {
        return classBytes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toByteArray()));
    }
}

