/*
 * */
package com.synectiks.process.server.utilities;

import com.github.joschi.jadconfig.Converter;
import com.github.joschi.jadconfig.ParameterException;

public class ProxyHostsPatternConverter implements Converter<ProxyHostsPattern> {
    @Override
    public ProxyHostsPattern convertFrom(String value) {
        try {
            return ProxyHostsPattern.create(value);
        } catch (IllegalArgumentException e) {
            throw new ParameterException("Invalid proxy hosts pattern: \"" + value + "\"", e);
        }
    }

    @Override
    public String convertTo(ProxyHostsPattern value) {
        return value.getNoProxyHosts();
    }
}
