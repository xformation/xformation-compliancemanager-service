/*
 * */
package com.synectiks.process.server.inputs.converters;

import org.apache.commons.codec.digest.DigestUtils;

import com.synectiks.process.server.plugin.inputs.Converter;

import java.util.Map;

public class HashConverter extends Converter {

    public HashConverter(Map<String, Object> config) {
        super(Type.HASH, config);
    }

    @SuppressWarnings("WEAK_MESSAGE_DIGEST_MD5")
    @Override
    public Object convert(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        // MessageDigest is not threadsafe. #neverForget
        return DigestUtils.md5Hex(value);
    }

    @Override
    public boolean buildsMultipleFields() {
        return false;
    }

}
