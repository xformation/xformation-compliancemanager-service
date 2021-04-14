/*
 * */
package com.synectiks.process.common.testing;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import com.synectiks.process.common.testing.mongodb.MongoDBExtension;
import com.synectiks.process.common.testing.mongodb.MongoDBTestService;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class TestUserServiceExtension implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return Objects.equals(TestUserService.class, parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        final Class<?> parameterType = parameterContext.getParameter().getType();

        if (TestUserService.class.equals(parameterType)) {
            final MongoDBTestService dbTestService = requireNonNull((MongoDBTestService) extensionContext.getStore(MongoDBExtension.NAMESPACE).get(MongoDBTestService.class));
            return new TestUserService(dbTestService.mongoConnection());
        }

        throw new ParameterResolutionException("Unsupported parameter type: " + parameterContext.getParameter().getName());
    }
}
