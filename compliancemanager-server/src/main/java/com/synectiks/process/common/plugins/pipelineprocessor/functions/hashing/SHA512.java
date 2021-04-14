/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.hashing;

import org.apache.commons.codec.digest.DigestUtils;

public class SHA512 extends SingleArgStringFunction {

    public static final String NAME = "sha512";

    @Override
    protected String getDigest(String value) {
        return DigestUtils.sha512Hex(value);
    }

    @Override
    protected String getName() {
        return NAME;
    }
}
