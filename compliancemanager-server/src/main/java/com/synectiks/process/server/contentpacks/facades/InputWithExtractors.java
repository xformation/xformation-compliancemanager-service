/*
 * */
package com.synectiks.process.server.contentpacks.facades;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.synectiks.process.server.inputs.Input;
import com.synectiks.process.server.plugin.inputs.Extractor;

@AutoValue
public abstract class InputWithExtractors {
    public abstract Input input();

    public abstract ImmutableList<Extractor> extractors();

    public static InputWithExtractors create(Input input, Iterable<Extractor> extractors) {
        return new AutoValue_InputWithExtractors(input, ImmutableList.copyOf(extractors));
    }

    public static InputWithExtractors create(Input input) {
        return create(input, ImmutableList.of());
    }
}
