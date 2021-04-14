/*
 * */
package com.synectiks.process.common.testing.mongodb;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

public class MongoJackExtension implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return isParameterSupported(parameterContext);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        if (isParameterSupported(parameterContext)) {
            return new MongoJackObjectMapperProvider(new ObjectMapperProvider().get());
        }
        throw new ParameterResolutionException("Unsupported parameter type: " + parameterContext.getParameter().getName());
    }

    private boolean isParameterSupported(ParameterContext parameterContext) {
        return MongoJackObjectMapperProvider.class.equals(parameterContext.getParameter().getType());
    }
}
