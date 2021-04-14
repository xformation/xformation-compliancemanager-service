/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.hashing;

import org.apache.commons.codec.digest.DigestUtils;

public class SHA1 extends SingleArgStringFunction {

    public static final String NAME = "sha1";

    @Override
    protected String getDigest(String value) {
        return DigestUtils.sha1Hex(value);
    }

    @Override
    protected String getName() {
        return NAME;
    }
}
