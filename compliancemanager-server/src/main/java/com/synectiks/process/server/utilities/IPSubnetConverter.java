/*
 * */
package com.synectiks.process.server.utilities;

import com.github.joschi.jadconfig.Converter;
import com.github.joschi.jadconfig.ParameterException;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.net.UnknownHostException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Converts a comma separated list of IP addresses / sub nets to set of {@link IpSubnet}.
 */
public class IPSubnetConverter implements Converter<Set<IpSubnet>> {
    @Override
    public Set<IpSubnet> convertFrom(String value) {
        final Set<IpSubnet> converted = new LinkedHashSet<>();
        if (value != null) {
            Iterable<String> subnets = Splitter.on(',').trimResults().split(value);
            for (String subnet : subnets) {
                try {
                    converted.add(new IpSubnet(subnet));
                } catch (UnknownHostException e) {
                    throw new ParameterException("Invalid subnet: " + subnet);
                }
            }
        }
        return converted;
    }

    @Override
    public String convertTo(Set<IpSubnet> value) {
        if (value == null) {
            throw new ParameterException("Couldn't convert IP subnets <null> to string.");
        }
        return Joiner.on(",").skipNulls().join(value);
    }
}
