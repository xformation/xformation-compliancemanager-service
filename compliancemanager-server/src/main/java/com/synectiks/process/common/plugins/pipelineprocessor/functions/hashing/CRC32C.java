/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.hashing;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class CRC32C extends SingleArgStringFunction {

    public static final String NAME = "crc32c";

    @Override
    protected String getDigest(String value) {
        return Hashing.crc32c().hashString(value, StandardCharsets.UTF_8).toString();
    }

    @Override
    protected String getName() {
        return NAME;
    }
}
