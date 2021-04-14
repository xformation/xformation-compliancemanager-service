/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.hashing;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5 extends SingleArgStringFunction {

    public static final String NAME = "md5";

    @Override
    protected String getDigest(String value) {
        return DigestUtils.md5Hex(value);
    }

    @Override
    protected String getName() {
        return NAME;
    }
}
