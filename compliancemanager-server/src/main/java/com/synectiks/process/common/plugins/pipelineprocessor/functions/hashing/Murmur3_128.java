/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.hashing;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class Murmur3_128 extends SingleArgStringFunction {

    public static final String NAME = "murmur3_128";

    @Override
    protected String getDigest(String value) {
        return Hashing.murmur3_128().hashString(value, StandardCharsets.UTF_8).toString();
    }

    @Override
    protected String getName() {
        return NAME;
    }
}
