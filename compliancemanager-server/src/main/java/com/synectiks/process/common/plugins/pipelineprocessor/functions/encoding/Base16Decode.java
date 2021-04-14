/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.encoding;

import com.google.common.io.BaseEncoding;

import java.nio.charset.StandardCharsets;

public class Base16Decode extends BaseEncodingSingleArgStringFunction {
    public static final String NAME = "base16_decode";
    private static final String ENCODING_NAME = "base16";

    @Override
    protected String getEncodedValue(String value, boolean omitPadding) {
        BaseEncoding encoding = BaseEncoding.base16();
        encoding = omitPadding ? encoding.omitPadding() : encoding;

        return new String(encoding.decode(value), StandardCharsets.UTF_8);
    }

    @Override
    protected String getEncodingName() {
        return ENCODING_NAME;
    }

    @Override
    protected String getName() {
        return NAME;
    }
}
