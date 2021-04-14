/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.ips;

import static com.google.common.collect.ImmutableList.of;

import com.google.common.net.InetAddresses;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import java.net.InetAddress;
import java.util.IllegalFormatException;
import java.util.Optional;

public class IpAddressConversion extends AbstractFunction<IpAddress> {

    public static final String NAME = "to_ip";
    private static final InetAddress ANYV4 = InetAddresses.forString("0.0.0.0");

    private final ParameterDescriptor<Object, Object> ipParam;
    private final ParameterDescriptor<String, String> defaultParam;

    public IpAddressConversion() {
        ipParam = ParameterDescriptor.object("ip").description("Value to convert").build();
        defaultParam = ParameterDescriptor.string("default").optional().description("Used when 'ip' is null or malformed, defaults to '0.0.0.0'").build();
    }

    @Override
    public IpAddress evaluate(FunctionArgs args, EvaluationContext context) {
        final Object ip = ipParam.required(args, context);
        try {
            if (ip instanceof Number) {
                // this is only valid for IPv4 addresses, v6 requires 128 bits which we don't support
                return new IpAddress(InetAddresses.fromInteger(((Number) ip).intValue()));
            } else {
                return new IpAddress(InetAddresses.forString(String.valueOf(ip)));
            }
        } catch (IllegalArgumentException e) {
            final Optional<String> defaultValue = defaultParam.optional(args, context);
            if (!defaultValue.isPresent()) {
                return new IpAddress(ANYV4);
            }
            try {
                return new IpAddress(InetAddresses.forString(defaultValue.get()));
            } catch (IllegalFormatException e1) {
                log.warn("Parameter `default` for to_ip() is not a valid IP address: {}", defaultValue.get());
                throw e1;
            }
        }
    }

    @Override
    public FunctionDescriptor<IpAddress> descriptor() {
        return FunctionDescriptor.<IpAddress>builder()
                .name(NAME)
                .returnType(IpAddress.class)
                .params(of(
                        ipParam,
                        defaultParam
                ))
                .description("Converts a value to an IPAddress using its string representation")
                .build();
    }
}
