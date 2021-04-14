/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.hashing;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class CRC32 extends SingleArgStringFunction {

    public static final String NAME = "crc32";

    @Override
    protected String getDigest(String value) {
        return Hashing.crc32().hashString(value, StandardCharsets.UTF_8).toString();
    }

    @Override
    protected String getName() {
        return NAME;
    }
}
