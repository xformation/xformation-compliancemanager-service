/*
 * */
package com.synectiks.process.server.configuration.converters;

import com.github.joschi.jadconfig.Converter;
import com.synectiks.process.server.plugin.Version;

public class MajorVersionConverter implements Converter<Version> {
    @Override
    public Version convertFrom(String value) {
        final int majorVersion = Integer.parseInt(value);
        return Version.from(majorVersion, 0, 0);
    }

    @Override
    public String convertTo(Version value) {
        return value.toString();
    }
}
