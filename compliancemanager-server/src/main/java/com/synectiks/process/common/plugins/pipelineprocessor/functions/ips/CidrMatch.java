/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.ips;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;
import com.synectiks.process.server.utilities.IpSubnet;

import java.net.UnknownHostException;

import static com.google.common.collect.ImmutableList.of;

public class CidrMatch extends AbstractFunction<Boolean> {

    public static final String NAME = "cidr_match";
    public static final String IP = "ip";

    private final ParameterDescriptor<String, IpSubnet> cidrParam;
    private final ParameterDescriptor<IpAddress, IpAddress> ipParam;

    public CidrMatch() {
        // a little ugly because newCIDR throws a checked exception :(
        cidrParam = ParameterDescriptor.string("cidr", IpSubnet.class).transform(cidrString -> {
            try {
                return new IpSubnet(cidrString);
            } catch (UnknownHostException e) {
                throw new IllegalArgumentException(e);
            }
        }).description("The CIDR subnet mask").build();
        ipParam = ParameterDescriptor.type(IP, IpAddress.class).description("The parsed IP address to match against the CIDR mask").build();
    }

    @Override
    public Boolean evaluate(FunctionArgs args, EvaluationContext context) {
        final IpSubnet cidr = cidrParam.required(args, context);
        final IpAddress ipAddress = ipParam.required(args, context);
        if (cidr == null || ipAddress == null) {
            return null;
        }
        return cidr.contains(ipAddress.inetAddress());
    }

    @Override
    public FunctionDescriptor<Boolean> descriptor() {
        return FunctionDescriptor.<Boolean>builder()
                .name(NAME)
                .returnType(Boolean.class)
                .params(of(
                        cidrParam,
                        ipParam))
                .description("Checks if an IP address matches a CIDR subnet mask")
                .build();
    }
}
